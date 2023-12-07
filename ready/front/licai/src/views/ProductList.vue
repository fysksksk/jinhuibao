<template>
  <div>
    <Header></Header>
    <div class="content clearfix">
      <!--排行榜-->
      <ul class="rank-list">
        <li v-for="(item,ind) in rank" :key="item.phone">
          <img src="@/assets/image/list-rank1.png" alt="" v-if="ind == 0 ">
          <img src="@/assets/image/list-rank2.png" alt="" v-else-if="ind == 1 ">
          <img src="@/assets/image/list-rank3.png" alt="" v-else>
          <p class="rank-list-phone">{{ item.phone }}</p>
          <span>{{ item.money }}元</span>
        </li>
      </ul>
      <!--产品列表-->
      <ul class="preferred-select clearfix">
        <li v-for="product in productList" :key="product.id">
          <h3 class="preferred-select-title">
            <span>{{ product.productName }}</span>
            <img src="@/assets/image/1-bg1.jpg" alt="">
          </h3>
          <div class="preferred-select-number">
            <p><b>{{ product.rate }}</b>%</p>
            <span>历史年化收益率</span>
          </div>
          <div class="preferred-select-date">
            <div>
              <span>投资周期</span>
              <p><b>{{ product.cycle }}</b>个月</p>
            </div>
            <div>
              <span>剩余可投资金额</span>
              <p><b>{{ product.leftProductMoney }}</b>元</p>
            </div>
          </div>
          <p class="preferred-select-txt">
            优选计划项目，投资回报周期{{ product.cycle }}个月，起点低，适合短期资金周转、对流动性要求高的投资人。
          </p>
          <a href="javascript:void(0)" @click="goLink('/page/product/detail',{productId: product.id})" class="preferred-select-btn">立即投资</a>
        </li>

      </ul>

      <!--分页-->
      <div class="page_box">
        <ul class="pagination">
          <li ><a href="javascript:void(0)" @click="first">首页</a></li>
          <li><a href="javascript:void(0)" @click="pre">上一页</a></li>
          <li class="active"><span>{{page.pageNo}}</span></li>
          <li><a href="javascript:void(0)" @click="next">下一页</a></li>
          <li><a href="javascript:void(0)" @click="last">尾页</a></li>
          <li class="totalPages"><span>共{{page.totalPage}}页</span></li>
        </ul>
      </div>

    </div>

    <Footer></Footer>
  </div>
</template>

<script>
import Header from "@/components/common/Header";
import Footer from "@/components/common/Footer";
import {doGet} from "@/api/httpRequest";
import layx from "vue-layx";
//获取参数
let  productType = 0 ;

export default {
  name: "ProductList",
  components: {
    // eslint-disable-next-line vue/no-unused-components
    Header,
    // eslint-disable-next-line vue/no-unused-components
    Footer
  },
  data() {
    return {
      rank: [
        {
          "phone": "",
          "money": 0
        }
      ],
      productList: [{
        id: 0,
        productName: "",
        rate: 0.0,
        cycle: 0,
        releaseTime: 0,
        productType: 0,
        productNo: "",
        productMoney: 0,
        leftProductMoney: 0,
        bidMinLimit: 0,
        bidMaxLimit: 0,
        productStatus: 0,
        productFullTime: "",
        productDesc: ""
      }],
      page:{
        pageNo: 1,
        pageSize: 0,
        totalPage: 0,
        totalRecord: 0
      }
    }
  },
  mounted() {
    productType =  this.$route.query.ptype;
    this.initPage(productType,1,9);

    //获取服务器数据
    doGet('/v1/invest/rank').then(resp => {
      if (resp) {
        this.rank = resp.data.list;
      }
    })
  },
  methods: {
    goLink(url,params){
      //使用router做页面跳转， vue中的对象
      this.$router.push({
        path: url,
        query: params
      })
    },
    initPage(productType,pNo,pSize) {
      //获取产品列表数据
      doGet('/v1/product/list', {ptype: productType, pageNo: pNo, pageSize: pSize})
          .then(resp => {
            if (resp) {
              this.productList = resp.data.list;
              this.page  = resp.data.page;
            }
          })
    },
    first(){
      if( this.page.pageNo == 1 ){
        layx.msg('已经是第一页数据.',{dialogIcon:'warn',position:'ct'});
      } else {
        this.initPage(productType,1,9)
      }
    },
    last(){
      if( this.page.pageNo == this.page.totalPage ){
        layx.msg('已经是最后一页数据.',{dialogIcon:'warn',position:'ct'});
      } else {
        this.initPage(productType,this.page.totalPage,9)
      }
    },
    pre(){
      if( this.page.pageNo <= 1 ){
        layx.msg('已经是第一页数据.',{dialogIcon:'warn',position:'ct'});
      } else {
        this.initPage(productType,this.page.pageNo - 1 , 9);
      }

    },
    next(){
      if( this.page.pageNo >= this.page.totalPage ){
        layx.msg('已经是最后一页数据.',{dialogIcon:'warn',position:'ct'});
      } else {
        this.initPage(productType,this.page.pageNo + 1, 9);
      }

    }
  }
}
</script>

<style scoped>

</style>