package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.User;
import com.bjpowernode.api.pojo.UserAccountInfo;
import com.bjpowernode.common.enums.RCode;
import com.bjpowernode.common.util.CommonUtil;
import com.bjpowernode.common.util.JwtUtils;
import com.bjpowernode.front.service.RealnameServiceImpl;
import com.bjpowernode.front.service.SmsService;
import com.bjpowernode.front.view.RespResult;
import com.bjpowernode.front.vo.RealnameVO;
import com.sun.corba.se.spi.ior.ObjectKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户功能")
@RequestMapping("/v1/user")
@RestController // 是@Controller和ResponseBody的结合，返回值默认转为JSON字符串
public class UserController extends BaseController{

    @Resource
    private RealnameServiceImpl realnameService;

    @Resource(name = "SmsCodeRealnameImpl")
    private SmsService SmsCodeRealnameImplService;

    @Resource(name = "smsCodeRegisterImpl")
    private SmsService smsService;

    @Resource(name = "smsCodeLoginImpl")
    private SmsService loginSmsService;

    // 在MicrWebApplication中已经创建了JwtUtils类，并给予了密钥
    @Resource
    private JwtUtils jwtUtils;

    // 用户中心
    @ApiOperation(value = "用户中心")
    @GetMapping("/usercenter")
    public RespResult userCenter(@RequestHeader(value = "uid", required = false) Integer uid) {
        RespResult result = RespResult.fail();
        if (uid != null && uid > 0) {
            UserAccountInfo userAccountInfo = userService.queryUserAllInfo(uid);
            if (userAccountInfo != null) {
                result = RespResult.ok();

                Map<String, Object> data = new HashMap<>();
                data.put("name", userAccountInfo.getName());
                data.put("phone", userAccountInfo.getPhone());
                data.put("headerUrl", userAccountInfo.getHeaderImage());
                data.put("money", userAccountInfo.getAvailableMoney());
                if (userAccountInfo.getLastLoginTime() != null) {
                    data.put("loginTime", DateFormatUtils.format(
                            userAccountInfo.getLastLoginTime(), "yyyy-MM-dd HH:mm:sss"));
                } else {
                    data.put("loginTime", "-");
                }
                result.setData(data);
            }
        }
        return result;
    }

    // 登录，获取token-jwt
    @ApiOperation(value = "用户登录-获取访问token")
    @PostMapping("/login")
    public RespResult userLogin(@RequestParam String phone,
                                @RequestParam String pword,
                                @RequestParam String scode) throws Exception{
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone) && (pword != null && pword.length() == 32)) {
            if (loginSmsService.checkSmsCode(phone, scode)) {
                // 访问data-service
                User user = userService.userLogin(phone, pword);
                if (user != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", user.getId());
                    String jwtToken = jwtUtils.createJwt(data, 120);

                    result = RespResult.ok();
                    result.setAccessToken(jwtToken);

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("uid", user.getId());
                    userInfo.put("phone", user.getPhone());
                    userInfo.put("name", user.getName());
                    result.setData(userInfo);
                } else {
                    result.setRCode(RCode.PHONE_LOGIN_PASSWORD_INVALID);
                }
            } else {
                result.setRCode(RCode.SMS_CODE_INVALID);
            }
        } else {
            result.setRCode(RCode.REQUEST_PARAM_ERR);
        }

        return result;
    }

    // 手机号注册用户
    @ApiOperation(value = "手机号注册用户")
    @PostMapping("/register")
    public RespResult userRegister(@RequestParam String phone,
                                   @RequestParam String pword,
                                   @RequestParam String scode) {
        RespResult result = RespResult.fail();
        // 检查参数
        if (CommonUtil.checkPhone(phone)) {
            if (pword != null && pword.length() == 32) {
                // 检查短信验证码
                if (smsService.checkSmsCode(phone, scode)) {
                    // 可以注册
                    int registerResult = userService.userRegister(phone, pword);
                    if (registerResult == 1) {
                        result = RespResult.ok();
                    } else if (registerResult == 2) {
                        result.setRCode(RCode.PHONE_EXISTS);
                    } else {
                        result.setRCode(RCode.REQUEST_PARAM_ERR);
                    }
                } else {
                    // 短信验证码无效
                    result.setRCode(RCode.SMS_CODE_INVALID);
                }
            } else {
                // 密码格式不正确
                result.setRCode(RCode.REQUEST_PARAM_ERR);
            }
        } else {
            // 手机号格式不正确
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }

    // 手机号是否存在
    @ApiOperation(value = "手机号是否注册过", notes = "在注册功能中，判断手机号是否可以注册")
    @ApiImplicitParam(name = "phone", value = "手机号")
    @GetMapping("/phone/exists")
    public RespResult phoneExists(@RequestParam("phone") String phone) {
        RespResult result = new RespResult();
        result.setRCode(RCode.PHONE_EXISTS);
        // 1、检查请求参数是否符合逻辑
        if (CommonUtil.checkPhone(phone)) {
            // 查询数据库，调用数据服务
            User user = userService.queryByPhone(phone);
            if (user == null) {
                // 可以注册
                result = RespResult.ok();
            }
        } else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }

    // 实名认证
    @ApiOperation(value = "实名认证", notes = "提供手机号、姓名和身份证。认证姓名和身份证是否一致")
    @PostMapping("/realname")
    public RespResult userRealname(@RequestBody RealnameVO realnameVO) {
        RespResult result = RespResult.fail();
        result.setRCode(RCode.REQUEST_PARAM_ERR);
        // 1、验证请求参数
        if (CommonUtil.checkPhone(realnameVO.getPhone())) {
            if (StringUtils.isNotBlank(realnameVO.getName()) &&
                        StringUtils.isNotBlank(realnameVO.getIdCard())) {
                if (SmsCodeRealnameImplService.checkSmsCode(realnameVO.getPhone(), realnameVO.getCode())) {
                    // 判断用户是否已经做过实名认证
                    User user = userService.queryByPhone(realnameVO.getPhone());
                    if (user.getName() != null) {
                        result.setRCode(RCode.REALNAME_RETRY);
                    } else {
                        // 调用第三方接口，判断认证结果
                        boolean realnameResult = realnameService.handleRealname(realnameVO.getPhone(),
                                realnameVO.getName(),
                                realnameVO.getIdCard());
                        if (realnameResult == true) {
                            result = RespResult.ok();
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("uid", user.getId());
                            userInfo.put("phone", user.getPhone());
                            userInfo.put("name", user.getName());
                            result.setData(userInfo);
                        } else {
                            result.setRCode(RCode.REALNAME_FAIL);
                        }
                    }
                }
            }
        }
        return result;
    }
}
