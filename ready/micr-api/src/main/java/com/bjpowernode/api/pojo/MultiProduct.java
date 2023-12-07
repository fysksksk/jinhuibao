package com.bjpowernode.api.pojo;

import com.bjpowernode.api.model.ProductInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Package:com.bjpowernode.api.pojo
 * Date:2022/3/3 15:04
 * 多个产品数据
 */
public class MultiProduct implements Serializable {
    private List<ProductInfo> xinShouBao;
    private List<ProductInfo> youXuan;
    private List<ProductInfo> sanBiao;

    public List<ProductInfo> getXinShouBao() {
        return xinShouBao;
    }

    public void setXinShouBao(List<ProductInfo> xinShouBao) {
        this.xinShouBao = xinShouBao;
    }

    public List<ProductInfo> getYouXuan() {
        return youXuan;
    }

    public void setYouXuan(List<ProductInfo> youXuan) {
        this.youXuan = youXuan;
    }

    public List<ProductInfo> getSanBiao() {
        return sanBiao;
    }

    public void setSanBiao(List<ProductInfo> sanBiao) {
        this.sanBiao = sanBiao;
    }
}
