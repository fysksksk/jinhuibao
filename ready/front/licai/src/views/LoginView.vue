<template>
  <div>
    <Header></Header>
    <div class="login-content">
      <div class="login-flex">
        <div class="login-left">
          <h3>加入动力金融网</h3>
          <p>坐享<span>{{ platInfo.historyAvgRate }}%</span>历史年化收益</p>
          <p>平台用户<span>{{ platInfo.registerUsers }}</span>位 </p>
          <p>累计成交金额<span>{{ platInfo.sumBidMoney }}</span>元</p>
        </div>
        <!---->
        <div class="login-box">
          <h3 class="login-title">欢迎登录</h3>
          <form action="" id="login_Submit">
            <div class="alert-input">
              <!--<input class="form-border user-name" name="username" type="text" placeholder="您的姓名">
              <p class="prompt_name"></p>-->
              <input type="text" class="form-border user-num" v-model="phone" @blur="checkPhone" name="mobile"
                     placeholder="请输入11位手机号">
              <div class="err">{{ phoneErr }}</div>
              <p class="prompt_num"></p>
              <input type="password" placeholder="请输入登录密码" class="form-border user-pass" v-model="password"
                     @blur="checkPassword" autocomplete name="password">
              <div class="err">{{ passwordErr }}</div>
              <p class="prompt_pass"></p>
              <div class="form-yzm form-border">
                <input class="yzm-write" type="text" v-model="code" @blur="checkCode" placeholder="输入短信验证码">
                <input class="yzm-send" type="button" value="获取验证码" id="yzmBtn" @click="requestSmsCode">
              </div>
              <div class="err">{{ codeErr }}</div>
              <p class="prompt_yan"></p>
            </div>
            <div class="alert-input-btn">
              <input type="button" @click="userLogin" class="login-submit" value="登录">
            </div>
          </form>

        </div>

      </div>
    </div>
    <Footer></Footer>

  </div>
</template>

<script>
import Footer from "@/components/common/Footer";
import Header from "@/components/common/Header";
import {doGet, doPost} from "@/api/httpRequest";
import layx from "vue-layx";
import md5 from 'js-md5';

export default {
  name: "LoginView",
  components: {
    // eslint-disable-next-line vue/no-unused-components
    Header,
    // eslint-disable-next-line vue/no-unused-components
    Footer
  },
  data() {
    return {
      platInfo: {historyAvgRate: 0.00, sumBidMoney: 0.00, registerUsers: 0},
      phone: '',
      phoneErr: '',
      password: '',
      passwordErr: '',
      code: '',
      codeErr: '',
    }
  },
  mounted() {
    doGet('/v1/plat/info').then(resp => {
      if (resp) {
        this.platInfo = resp.data.data;
      }
    })
  },
  methods: {
    checkPhone() {
      if (this.phone == '' || this.phone == undefined) {
        this.phoneErr = '请输入手机号';
      } else if (this.phone.length != 11) {
        this.phoneErr = '手机号长度不足11位';
      } else if (!/^1[1-9]\d{9}$/.test(this.phone)) {
        this.phoneErr = '手机号格式不正确'
      } else {
        this.phoneErr = '';
      }
    },
    checkPassword() {
      if (this.password == '' || this.password == undefined) {
        this.passwordErr = '请输入密码';
      } else if (this.password.length < 6 || this.password.length > 20) {
        this.passwordErr = '密码长度是6-20位';
      } else if (!/^[0-9a-zA-Z]+$/.test(this.password)) {
        this.passwordErr = '密码只能使用数字和字母';
      } else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(this.password)) {
        this.passwordErr = '密码是数字和字母的混合';
      } else {
        this.passwordErr = '';
      }
    },
    checkCode() {
      if (this.code == '' || this.code == undefined) {
        this.codeErr = '必须输入验证码';
      } else if (this.code.length != 4) {
        this.codeErr = '验证码是4位的';
      } else {
        this.codeErr = '';
      }
    },
    requestSmsCode() {
      this.checkPhone();
      if (this.phoneErr == '') {
        //调用服务器发到短信的接口  /code/login
        doGet('/v1/sms/code/login', {phone: this.phone}).then(resp => {
          if (resp && resp.data.code == 1000) {
            layx.msg('短信已经发送了', {dialogIcon: 'success', position: 'ct'});
          }
        })
      }
    },
    userLogin() {
      this.checkPhone();
      this.checkPassword();
      this.checkCode();
      if (this.phoneErr == '' && this.passwordErr == '' & this.codeErr == '') {
        //发起登录请求
        let param = {
          phone: this.phone, pword: md5(this.password), scode: this.code
        }
        doPost('/v1/user/login', param).then(resp => {
          if (resp && resp.data.code == 1000) {
            //登录成功，存储数据到localStorage,存的是字符串
            window.localStorage.setItem("token", resp.data.accessToken);
            //把 json对象转为 string
            window.localStorage.setItem("userinfo", JSON.stringify(resp.data.data));
            console.log(JSON.stringify(resp.data.data));
            //登录之后，如果name没有值，需要进入到实名认证页面
            //如果name有值，进入到用户中心
            if (resp.data.data.name == '' || resp.data.data.name == null) {
              //实名认证
              this.$router.push({
                path: '/page/user/realname'
              })
            } else {
              //用户中心
              this.$router.push({
                path: '/page/user/usercenter'
              })
            }
          }
        })
      }
    }
  }
}
</script>

<style scoped>
.err {
  color: red;
  font-size: 18px;
}
</style>