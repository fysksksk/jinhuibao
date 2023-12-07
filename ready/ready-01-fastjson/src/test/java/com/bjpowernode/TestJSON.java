package com.bjpowernode;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.model.School;
import com.bjpowernode.model.Student;
import org.junit.Test;

public class TestJSON {

    @Test
    public void test01() {
        School school = new School("北京大学", "北京海淀区");
        Student student = new Student(1001, "李四", 20, school);

        // student -> json
        String json = JSONObject.toJSONString(student);
        System.out.println(json);
    }

    @Test
    public void test02() {
        String json = "{\"age\":20,\"id\":1001,\"name\":\"李四\",\"school\":{\"address\":\"北京海淀区\",\"name\":\"北京大学\"}}";
        // json -> student
        Student student = JSONObject.parseObject(json, Student.class);
        System.out.println("student = " + student);
    }

    @Test
    public void testRead() {
        String json = "{\"age\":20,\"id\":1001,\"name\":\"李四\",\"school\":{\"address\":\"北京海淀区\",\"name\":\"北京大学\"}}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject schoolObject = jsonObject.getJSONObject("school");
        String address = schoolObject.getString("address");
        System.out.println("address = " + address);
    }
}
