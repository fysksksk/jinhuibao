package com.bjpowernode.front.controller;

import com.bjpowernode.common.constants.RedisKey;
import com.bjpowernode.common.constants.YLBConstant;
import com.bjpowernode.common.enums.RCode;
import com.bjpowernode.common.util.CommonUtil;
import com.bjpowernode.front.service.SmsService;
import com.bjpowernode.front.view.RespResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "短信业务")
@RestController // 是@Controller和ResponseBody的结合，返回值默认转为JSON字符串
@RequestMapping("/v1/sms")
public class SmsController extends BaseController {

    @Resource(name = "smsCodeRegisterImpl")
    private SmsService smsService;

    @Resource(name = "smsCodeLoginImpl")
    private SmsService loginSmsService;

    @Resource(name = "SmsCodeRealnameImpl")
    private SmsService realnameService;

    // 发送实名认证验证码短信
    @GetMapping("/code/realname")
    public RespResult sendCodeRealname(@RequestParam("phone") String phone) {
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone)) {
            // 判断redis中是否有这个手机号的验证码
            String key = RedisKey.KEY_SMS_CODE_REALNAME + phone;
            if (stringRedisTemplatem.hasKey(key)) {
                result = RespResult.ok();
                result.setRCode(RCode.SMS_CODE_CAN_USE);
            } else {
                boolean isSuccess = realnameService.sendSms(phone);
                if (isSuccess) {
                    result = RespResult.ok();
                }
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }

    // 发送注册验证码短信
    @GetMapping("/code/register")
    public RespResult sendCodeRegister(@RequestParam("phone") String phone) {
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone)) {
            // 判断redis中是否有这个手机号的验证码
            String key = RedisKey.KEY_SMS_CODE_REG + phone;
            if (stringRedisTemplatem.hasKey(key)) {
                result = RespResult.ok();
                result.setRCode(RCode.SMS_CODE_CAN_USE);
            } else {
                boolean isSuccess = smsService.sendSms(phone);
                if (isSuccess) {
                    result = RespResult.ok();
                }
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }

    /**发送登录验证码短信*/
    @GetMapping("/code/login")
    public RespResult sendCodeLogin(@RequestParam String phone){
        RespResult result = RespResult.fail();
        if(CommonUtil.checkPhone(phone)){
            //判断redis中是否有这个手机号的验证码
            String key  = RedisKey.KEY_SMS_CODE_LOGIN + phone;
            if(stringRedisTemplatem.hasKey(key)){
                result = RespResult.ok();
                result.setRCode(RCode.SMS_CODE_CAN_USE);
            } else {
                boolean isSuccess = loginSmsService.sendSms(phone);
                if( isSuccess ){
                    result = RespResult.ok();
                }
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }
}
