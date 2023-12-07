package com.bjpowernode.api.service;

import com.bjpowernode.api.pojo.BaseInfo;

public interface PlatBaseInfoService {

    /**
     * 计算利率，注册人数，累计成交额
     */
    BaseInfo queryPlatBaseInfo();
}
