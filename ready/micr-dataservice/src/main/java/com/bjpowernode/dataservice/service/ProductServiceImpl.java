package com.bjpowernode.dataservice.service;

import com.bjpowernode.api.model.ProductInfo;
import com.bjpowernode.api.pojo.MultiProduct;
import com.bjpowernode.api.service.ProductService;
import com.bjpowernode.common.constants.YLBConstant;
import com.bjpowernode.common.util.CommonUtil;
import com.bjpowernode.dataservice.mapper.ProductInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = ProductService.class,version = "1.0")
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductInfoMapper productInfoMapper;

    // 按类型分页查询产品
    @Override
    public List<ProductInfo> queryByTypeLimit(Integer pType, Integer pageNo, Integer pageSize) {

        List<ProductInfo> productInfos = new ArrayList<>();
        if (pType == 0 || pType == 1 || pType == 2) {
            pageNo = CommonUtil.defaultPageNO(pageNo);
            pageSize = CommonUtil.defaultPageSize(pageSize);
            int offset = (pageNo - 1) * pageSize;
            productInfos = productInfoMapper.selectByTypeLimit(pType, offset, pageSize);
        }
        return productInfos;
    }

    // 某个产品类型的记录总数
    @Override
    public Integer queryRecordNumsByType(Integer pType) {
        Integer counts = 0;
        if (pType == 0 || pType == 1 || pType == 2) {
            counts = productInfoMapper.selectCountByType(pType);
        }
        return counts;
    }

    // 首页的所有产品数据
    @Override
    public MultiProduct queryIndexPageProducts() {
        MultiProduct result = new MultiProduct();

        // 查询新手宝
        List<ProductInfo> xinShouBaoList = productInfoMapper.selectByTypeLimit(
                                    YLBConstant.PRODUCT_TYPE_XINSHOBAO, 0, 1);

        // 查询优选
        List<ProductInfo> youXuanList = productInfoMapper.selectByTypeLimit(
                                    YLBConstant.PRODUCT_TYPE_YOUXUANBAO, 0, 3);

        // 散标
        List<ProductInfo> sanBiaoList = productInfoMapper.selectByTypeLimit(
                                    YLBConstant.PRODUCT_TYPE_SANBIAOBAO, 0, 3);

        result.setXinShouBao(xinShouBaoList);
        result.setYouXuan(youXuanList);
        result.setSanBiao(sanBiaoList);

        return result;
    }

    /*根据产品id，查询产品信息*/
    @Override
    public ProductInfo queryById(Integer id) {
        ProductInfo productInfo = null;
        if (id != null && id > 0) {
            productInfo = productInfoMapper.selectByPrimaryKey(id);
        }
        return productInfo;
    }
}
