package com.bjpowernode.api.service;

import com.bjpowernode.api.model.RechargeRecord;

import java.util.List;

public interface RechargeService {

    // 处理后续充值
    int handleKQNotify(String orderId, String payAmount, String payResult);

    // 根据userID查询用户的充值记录
    List<RechargeRecord> queryByUid(Integer uid, Integer pageNo, Integer pageSize);

    // 创建充值记录
    int addRechargeRecord(RechargeRecord record);
}
