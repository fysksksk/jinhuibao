package com.bjpowernode.api.service;

import com.bjpowernode.api.model.User;
import com.bjpowernode.api.pojo.UserAccountInfo;

public interface UserService {

    // 查询用户
    User queryById(Integer uid);

    // 获取用户和资金信息
    UserAccountInfo queryUserAllInfo(Integer uid);

    // 根据手机号查询数据
    User queryByPhone(String phone);

    // 用户注册
    int userRegister(String phone, String password);

    // 登录(查询登录信息)
    User userLogin(String phone, String pword);

    // 更新实名认证的信息
    boolean modifyRealname(String phone, String name, String idCard);
}
