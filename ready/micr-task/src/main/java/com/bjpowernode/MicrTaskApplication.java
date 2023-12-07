package com.bjpowernode;

import com.bjpowernode.task.TackManager;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDubbo
// 启用定时任务
@EnableScheduling
@SpringBootApplication
public class MicrTaskApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MicrTaskApplication.class, args);
        TackManager tm = (TackManager) ctx.getBean("taskManager");
        // tm.invokeGenerateIncomePlan();
        // tm.invokeGenerateIncomeBack();
        tm.invokeKuaiQianQuery();
    }
}
