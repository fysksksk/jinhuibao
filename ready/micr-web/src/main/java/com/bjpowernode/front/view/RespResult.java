package com.bjpowernode.front.view;

import com.bjpowernode.common.enums.RCode;

import java.util.List;

public class RespResult {
    // 访问token
    private String accessToken;
    // 应答码，自定义的数字
    private int code;
    // code的文字说明，一般做提示给用户看
    private String msg;
    // 单个数据
    private Object data;
    // 集合数据
    private List list;
    // 分页
    private PageInfo page;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    public int getCode() {
        return code;
    }

    // 表示成功的RespResult对象
    public static RespResult ok() {
        RespResult result = new RespResult();
        result.setRCode(RCode.SUCC);
        return result;
    }

    // 表示失败的RespResult对象
    public static RespResult fail() {
        RespResult result = new RespResult();
        result.setRCode(RCode.UNKOWN);
        return result;
    }

    public void setRCode(RCode rCode) {
        this.code = rCode.getCode();
        this.msg = rCode.getText();
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
