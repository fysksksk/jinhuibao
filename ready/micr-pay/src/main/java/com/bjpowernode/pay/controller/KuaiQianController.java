package com.bjpowernode.pay.controller;

import com.bjpowernode.api.model.User;
import com.bjpowernode.pay.service.KuaiQianService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/kq")
public class KuaiQianController {

    @Resource
    private KuaiQianService KQService;

    // 接收来自vue项目的支付充值请求
    @GetMapping("/rece/recharge")
    public String receFrontRechargeKQ(Integer uid,
                                      BigDecimal rechargeMoney,
                                      Model model) {
        // 默认是错误的
        String view = "err";
        if (uid != null && uid > 0 &&
                rechargeMoney != null && rechargeMoney.doubleValue() > 0) {

            try {
                // 1、检查uid是否是有效的用户
                User user = KQService.queryUser(uid);
                if (user != null) {
                    // 创建快钱支付接口需要的请求参数
                    Map<String, String> data = KQService.generateFormData(uid, user.getPhone(), rechargeMoney);
                    model.addAllAttributes(data);
                    // 创建充值记录
                    KQService.addRecharge(uid, rechargeMoney, data.get("orderId"));
                    // 把订单号存放到redis
                    KQService.addOrderIdToRedis(data.get("orderId"));

                    // 提交支付请求给快钱的form页面(thymeleaf)
                    view = "kqForm";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    // 接收快钱给商家的支付结果，快钱以get方式，发送请求给商家
    @GetMapping("/rece/notify")
    @ResponseBody
    public String payResultNotify(HttpServletRequest request) {
        System.out.println("收到异步通知");
        KQService.kqNotify(request);
        return "<result>1</result><redirecturl>http://192.168.43.123:8080/</redirecturl>";
    }

    // 定时任务调用的接口
    @GetMapping("/rece/query")
    @ResponseBody
    public String queryKQOrder() {
        KQService.handleQueryOrder();
        return "接收了查询的请求";
    }
}
