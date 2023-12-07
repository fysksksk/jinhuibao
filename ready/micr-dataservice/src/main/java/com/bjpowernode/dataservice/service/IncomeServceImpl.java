package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.BidInfo;
import com.bjpowernode.api.model.IncomeRecord;
import com.bjpowernode.api.model.ProductInfo;
import com.bjpowernode.api.service.IncomeService;
import com.bjpowernode.common.constants.YLBConstant;
import com.bjpowernode.dataservice.mapper.BidInfoMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.IncomeRecordMapper;
import com.bjpowernode.dataservice.mapper.ProductInfoMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = IncomeService.class, version = "1.0")
public class IncomeServceImpl implements IncomeService {

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private ProductInfoMapper productInfoMapper;

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private IncomeRecordMapper incomeMapper;

    // 收益计划
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void generateIncomePlan() {
        // 1、获取要处理的理财产品记录
        Date currentDate = new Date();
        Date beginTime = DateUtils.truncate(DateUtils.addDays(currentDate, -1), Calendar.DATE);
        Date endTime = DateUtils.truncate(currentDate, Calendar.DATE);
        List<ProductInfo> productInfoList = productInfoMapper.selectFullTimeProducts(beginTime, endTime);

        // 2、查询每个理财产品的多个投资记录
        int rows  = 0 ;
        BigDecimal income =  null;
        BigDecimal dayRate = null;
        BigDecimal cycle  = null; //周期

        Date incomeDate  = null;//到期时间
        for (ProductInfo product : productInfoList) {
            // 日利率
            dayRate = product.getRate().divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

            // 产品类型不同，周期不同
            if (product.getProductType() == YLBConstant.PRODUCT_TYPE_XINSHOBAO) {
                cycle = new BigDecimal(product.getCycle());
                incomeDate = DateUtils.addDays(product.getProductFullTime(), (1 + product.getCycle()));
            } else {
                cycle = new BigDecimal(product.getCycle() * 30);
                incomeDate = DateUtils.addDays(product.getProductFullTime(), (1 + product.getCycle() * 30));
            }
            List<BidInfo> bidList = bidInfoMapper.queryByProductId(product.getId());
            // 3、计算每笔投资的 利息 和 到期时间
            for (BidInfo bid : bidList) {
                // 利息 = 本金 * 周期 * 利率
                income = bid.getBidMoney().multiply(cycle).multiply(dayRate);
                // 创建收益记录
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bid.getId());
                incomeRecord.setBidMoney(bid.getBidMoney());
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeStatus(YLBConstant.INCOME_STATUS_PLAN);
                incomeRecord.setProdId(product.getId());
                incomeRecord.setIncomeMoney(income);
                incomeRecord.setUid(bid.getUid());
                incomeMapper.insertSelective(incomeRecord);
            }
            // 更新产品的状态
            rows = productInfoMapper.updateStatus(product.getId(), YLBConstant.PRODUCT_STATUS_PLAN);
            if (rows < 1) {
                throw new RuntimeException("生成收益计划，更新产品状态为2失败");
            }
        }
    }

    // 收益返还
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void generateIncomeBack() {
        // 1、获取要处理的到期的收益计划
        Date curDate = new Date();
        Date expiredDate = DateUtils.truncate(DateUtils.addDays(curDate, -1), Calendar.DATE);
        List<IncomeRecord> incomeRecordList = incomeMapper.selectExpiredIncome(expiredDate);

        int rows = 0;
        // 2、把每个收益进行返还，本金 + 利息
        for (IncomeRecord ir : incomeRecordList) {
            rows = accountMapper.updateAvailableMoneyByIncomeBack(ir.getUid(), ir.getBidMoney(), ir.getIncomeMoney());
            if (rows < 1) {
                throw new RuntimeException("收益返还，更新账号资金失败");
            }

            // 3、更新收益记录的状态为 1
            ir.setIncomeStatus(YLBConstant.INCOME_STATUS_BACK);
            rows = incomeMapper.updateByPrimaryKey(ir);
            if (rows < 1) {
                throw new RuntimeException("收益返还，更新收益记录的状态失败");
            }
        }
    }
}
