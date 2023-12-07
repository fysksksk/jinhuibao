package com.bjpowernode.front.service;

public interface SmsService {

    /**
     * @param phone 手机号
     * @param code 验证码
     * @return
     */
    boolean checkSmsCode(String phone, String code);

    /**
     * @param phone 手机号
     * @return true：发送成功，false：其它情况
     */
    boolean sendSms(String phone);
}
