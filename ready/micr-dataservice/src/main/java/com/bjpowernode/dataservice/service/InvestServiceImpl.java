package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.BidInfo;
import com.bjpowernode.api.model.FinanceAccount;
import com.bjpowernode.api.model.ProductInfo;
import com.bjpowernode.api.pojo.BidInfoProduct;
import com.bjpowernode.api.service.InvestService;
import com.bjpowernode.common.constants.YLBConstant;
import com.bjpowernode.common.util.CommonUtil;
import com.bjpowernode.dataservice.mapper.BidInfoMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.ProductInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = InvestService.class,version = "1.0")
public class InvestServiceImpl implements InvestService {

    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private ProductInfoMapper productInfoMapper;

    /*查询某个产品的投资记录*/
    @Override
    public List<BidInfoProduct> queryBidListByProductId(Integer productId,
                                                        Integer pageNo,
                                                        Integer pageSize) {
        List<BidInfoProduct> bidList = new ArrayList<>();
        if (productId != null && productId > 0) {
            pageNo = CommonUtil.defaultPageNO(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            bidList = bidInfoMapper.selectByProductId(productId, offset, pageSize);
        }
        return bidList;
    }

    // 投资理财产品，返回投资结果，1表示投资成功
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int investProduct(Integer uid, Integer productId, BigDecimal money) {
        int result = 0; // 默认参数不正确
        int rows = 0;
        // 1、参数检查
        if ((uid != null && uid > 0) && (productId != null && productId > 0)
                && (money != null)) {
            // 2、查询账号金额
            FinanceAccount account = accountMapper.selectByUidForUpdate(uid);
            if (account != null) {
                if (CommonUtil.ge(account.getAvailableMoney(), money)) {
                    // 资金满足购买要求
                    // 3、检查产品是否可以购买
                    ProductInfo productInfo = productInfoMapper.selectByPrimaryKey(productId);
                    if (productInfo != null && productInfo.getProductStatus() == YLBConstant.PRODUCT_STATUS_SELLING) {
                        if (CommonUtil.ge(productInfo.getLeftProductMoney(), money)) {
                            // 4、可以购买了，扣除账户资金
                            rows = accountMapper.updateAvailableMoneyByInvest(uid, money);
                            if (rows < 1) {
                                throw new RuntimeException("更新账号资金失败");
                            }
                            // 5、扣除产品剩余可投资金额
                            rows = productInfoMapper.updateLeftProductMoney(productId, money);
                            if (rows < 1) {
                                throw new RuntimeException("更新产品剩余金额失败");
                            }
                            // 6、创建投资记录
                            BidInfo bidInfo = new BidInfo();
                            bidInfo.setBidMoney(money);
                            bidInfo.setBidStatus(YLBConstant.INVEST_STATUS_SUCC);
                            bidInfo.setBidTime(new Date());
                            bidInfo.setProdId(productId);
                            bidInfo.setUid(uid);
                            bidInfoMapper.insertSelective(bidInfo);
                            // 7、判断产品是否卖完，更新产品为满标状态
                            ProductInfo dbProductInfo = productInfoMapper.selectByPrimaryKey(productId);
                            if (dbProductInfo.getLeftProductMoney().compareTo(new BigDecimal("0")) == 0) {
                                rows = productInfoMapper.updateSelled(productId);
                                if (rows < 1) {
                                    throw new RuntimeException("投资更新产品满标失败");
                                }
                            }
                            // 8、投资成功
                            result = 1;
                        } else {
                            result = 5;
                        }
                    } else {
                        result = 4; // 理财产品不存在
                    }
                } else {
                    result = 3; // 资金不足
                }
            } else {
                result = 2; // 资金账号不存在
            }
        }

        return result;
    }
}
