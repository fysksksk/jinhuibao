package com.bjpowernode.front.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Package:com.bjpowernode.front.config
 * Date:2022/3/9 11:14
 */
@Component
@ConfigurationProperties(prefix = "jdwx.realname")
public class JdwxRealnameConfig {
    private String url;
    private String appkey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
