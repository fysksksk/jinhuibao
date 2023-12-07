package com.bjpowernode;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TestClient {

    @Test
    public void testGet() {
        // 发起get请求，编程方式处理http请求
        // 用户ip
        String url = "https://restapi.amap.com/v3/ip?key=0113a13c88697dcea6a445584d535837&ip=60.25.188.64";
        // 1、创建HttpClient对象
        CloseableHttpClient client = HttpClients.createDefault();
        // 2、创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        // 3、执行请求，使用client对象的方法，执行请求后获取返回结果
        try {
            // CloseableHttpResponse是返回结果，相当于HttpServletResponse
            CloseableHttpResponse response = client.execute(httpGet);
            // 从response获取应答信息
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 4、获取数据
                // response.getEntity().getContent(); // 数据
                String json = EntityUtils.toString(response.getEntity());
                System.out.println("访问ip的应答结果：" + json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 5、关闭资源
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testHttpPost() {
        // 1、创建HttpClient对象
        CloseableHttpClient client = HttpClients.createDefault();

        // 2、创建HttpPost对象，表示post请求
        String url = "https://restapi.amap.com/v3/ip";
        HttpPost httpPost = new HttpPost(url);

        // 3、准备post请求的参数
        List<NameValuePair> params = new ArrayList<>();
        // 添加参数     BasicNameValuePair类，实现了NameValuePair接口
        //            BasicNameValuePair(参数名, 参数值);
        params.add(new BasicNameValuePair("key", "0113a13c88697dcea6a445584d535837"));
        // params.add(new BasicNameValuePair("ip", "60.25.188.64"));
        params.add(new BasicNameValuePair("ip", "111.201.50.56"));

        try {
            // 4、设置HttpPost的使用参数
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            // 5、执行请求
            CloseableHttpResponse res = client.execute(httpPost);
            // 6、读取数据
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String json = EntityUtils.toString(res.getEntity());
                System.out.println("json = " + json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
