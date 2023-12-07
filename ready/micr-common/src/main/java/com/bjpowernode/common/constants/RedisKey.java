package com.bjpowernode.common.constants;

public class RedisKey {

    // 投资排行榜
    public static final String KEY_INVEST_RANK = "INVEST:RANK";

    // 实名认证时，短信验证码存入redis
    public static final String KEY_SMS_CODE_REALNAME = "SMS:REALNAME:";

    // 注册时，短信验证码存入redis
    public static final String KEY_SMS_CODE_REG = "SMS:REG:";

    // 登录时，短信验证码存入redis
    public static final String KEY_SMS_CODE_LOGIN = "SMS:LOGIN:";

    // redis自增
    public static final String KEY_RECHARGE_ORDERID = "RECHARGE:ORDERID:SEQ";

    // orderId
    public static final String KEY_ORDERID_SET = "RECHARGE:ORDERID:SET";
}
