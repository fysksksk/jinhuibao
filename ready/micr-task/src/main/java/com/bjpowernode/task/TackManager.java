package com.bjpowernode.task;

import com.bjpowernode.api.service.IncomeService;
import com.bjpowernode.common.util.HttpClientUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("taskManager")
public class TackManager {

    /**
     * 定义方法，表示要执行的定时任务功能
     * 方法定义的要求：
     * 1.public 公共方法
     * 2.方法没有参数
     * 3.方法没有返回值
     * 4.方法的上面加入@Scheduled，设置cron属性，指定时间
     */
    // @Scheduled(cron = "*/5 48 15 * * ?")
    /*public void testCron() {
        System.out.println("执行了定时任务的方法：" + new Date());
    }*/

    @DubboReference(interfaceClass = IncomeService.class, version = "1.0")
    private IncomeService incomeService;

    // 生成收益计划
    @Scheduled(cron = "0 0 1 * * ?")
    public void invokeGenerateIncomePlan() {
        incomeService.generateIncomePlan();
    }

    // 收益返还
    @Scheduled(cron = "0 0 2 * * ?")
    public void invokeGenerateIncomeBack() {
        incomeService.generateIncomeBack();
    }

    // 补单接口
    @Scheduled(cron = "0 0/20 * * * ?")
    public void invokeKuaiQianQuery() {
        try {
            String url = "http://192.168.43.123:9000/pay/kq/rece/query";
            HttpClientUtils.doGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
