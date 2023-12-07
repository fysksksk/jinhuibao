package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.model.ProductInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductInfoMapper {

    // 更新产品状态
    int updateStatus(@Param("id") Integer id, @Param("newStatus") int newStatus);

    // 查找满标的理财列表
    List<ProductInfo> selectFullTimeProducts(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    // 更新产品为满标
    int updateSelled(Integer id);

    // 扣除产品剩余可投资金额
    int updateLeftProductMoney(@Param("id") Integer productId, @Param("money") BigDecimal money);

    // 按产品类型分页查询
    List<ProductInfo> selectByTypeLimit(@Param("ptype") Integer ptype,
                                        @Param("offset") Integer offset,
                                        @Param("rows") Integer rows);

    // 某个产品类型的记录总数
    Integer selectCountByType(@Param("ptype") Integer pType);

    // 利率的平均值
    BigDecimal selectAvgRate();

    int deleteByPrimaryKey(Integer id);

    int insert(ProductInfo record);

    int insertSelective(ProductInfo record);

    ProductInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInfo record);

    int updateByPrimaryKey(ProductInfo record);
}