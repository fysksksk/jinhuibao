package com.bjpowernode;

import com.bjpowernode.model.Student;
import org.junit.Test;
import com.alibaba.fastjson.*;

public class MyTest {

    // student对象转为json
    @Test
    public void testToJson() {
        Student student = new Student(2001, "李四", 20);
        // student转换为json
        String json = JSONObject.toJSONString(student);
        System.out.println("student转为json = " + json);
    }

    // json转为student对象
    @Test
    public void testToObject() {
        String json = "{\"age\" : 30, \"id\": 5671, \"name\": \"周畅\"}";
        // 将json转为对象
        Student student = JSONObject.parseObject(json, Student.class);
        System.out.println("student = " + student);
    }

    // 获取 key value
    @Test
    public void testAccessValue() {
        String json = "{\"age\" : 30, \"id\": 5671, \"name\": \"周畅\"}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        // jsonObject是一个map
        String name = jsonObject.getString("name");
        System.out.println("name = " + name);

        int age = jsonObject.getIntValue("age");
        System.out.println("age = " + age);
    }

}
