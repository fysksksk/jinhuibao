package com.bjpowernode.front.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.api.service.UserService;
import com.bjpowernode.common.util.HttpClientUtils;
import com.bjpowernode.front.config.JdwxRealnameConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RealnameServiceImpl {

    @Resource
    private JdwxRealnameConfig realnameConfig;

    @DubboReference(interfaceClass = UserService.class, version = "1.0")
    private UserService userService;

    // true：认证通过
    public boolean handleRealname(String phone, String name, String idCard) {

        boolean realname = false;
        Map<String, String> params = new HashMap<>();
        params.put("cardNo", idCard);
        params.put("realName", name);
        params.put("appkey", realnameConfig.getAppkey());
        try {
            // String resp = HttpClientUtils.doGet(realnameConfig.getUrl(), params);
            String resp="{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \""+name+"\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            if (StringUtils.isNotBlank(resp)) {
                JSONObject respObject = JSONObject.parseObject(resp);
                if ("10000".equalsIgnoreCase(respObject.getString("code"))) {
                    // 解析result
                    realname = respObject.getJSONObject("result")
                                    .getJSONObject("result")
                                    .getBooleanValue("isok");

                    // 更新数据库
                    boolean modifyResult = userService.modifyRealname(phone, name, idCard);
                    realname = modifyResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realname;
    }
}
