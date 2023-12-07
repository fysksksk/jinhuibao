package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.model.RechargeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeRecordMapper {

    // 更新充值记录的状态
    int updateStatus(@Param("id") Integer id, @Param("newStatus") int rechargeStatusSuccess);

    // 查询订单
    RechargeRecord selectByRechargeNo(@Param("rechargeNo") String orderId);

    // 根据userID查询用户的充值记录
    List<RechargeRecord> selectByUid(@Param("uid") Integer uid, @Param("offset") int offset, @Param("rows") Integer rows);

    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);
}