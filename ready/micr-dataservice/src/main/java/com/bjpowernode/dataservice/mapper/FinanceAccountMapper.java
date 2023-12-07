package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    // 搜索账号，并给uid的记录上锁
    FinanceAccount selectByUidForUpdate(@Param("uid") Integer uid);

    // 更新资金
    int updateAvailableMoneyByInvest(@Param("uid") Integer uid, @Param("money") BigDecimal money);

    // 返还收益给账户
    int updateAvailableMoneyByIncomeBack(@Param("id") Integer id,
                                         @Param("bidMoney") BigDecimal bidMoney,
                                         @Param("incomeMoney") BigDecimal incomeMoney);

    // 更新充值金额
    int updateAvailableMoneyByRecharge(@Param("uid") Integer uid, @Param("rechargeMoney") BigDecimal rechargeMoney);
}