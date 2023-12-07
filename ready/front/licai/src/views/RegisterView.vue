<template>
  <div>
    <Header></Header>
    <div class="login-content">
      <div class="login-flex">
        <div class="login-left">
          <p>万民用户知心托付&nbsp;&nbsp;&nbsp;&nbsp;<span>{{ historyAvgRate }}%</span>历史年化收益</p>
          <p>千万级技术研发投入&nbsp;&nbsp;&nbsp;&nbsp;亿级注册资本平台 </p>
        </div>
        <!---->
        <div class="login-box">
          <h3 class="login-title">用户注册</h3>
          <form action="" id="register_Submit">
            <div class="alert-input">
              <input type="text" class="form-border user-num" @blur="checkPhone" v-model="phone" name="mobile"
                     placeholder="请输入11位手机号">
              <div class="err">{{ phoneErr }}</div>
              <p class="prompt_num"></p>
              <input type="password" placeholder="请输入6-20位英文和数字混合密码"
                     class="form-border user-pass" autocomplete name="password" v-model="password"
                     @blur="checkPassword">
              <div class="err">{{ passwordErr }}</div>
              <p class="prompt_pass"></p>
              <div class="form-yzm form-border">
                <input class="yzm-write" type="text" name="" placeholder="输入短信验证码" v-model="code" @blur="checkCode">
                <input class="yzm-send" type="button" v-bind:value="yzmText" @click="requestSmsCode" id="yzmBtn">
              </div>
              <div class="err">{{ codeErr }}</div>

              <p class="prompt_yan"></p>
            </div>
            <div class="alert-input-agree">
              <input type="checkbox" v-model="agree">我已阅读并同意<a href="javascript:;" target="_blank">《动力金融网注册服务协议》</a>
            </div>
            <div class="alert-input-btn">
              <input type="button" class="login-submit" @click="requestUserRegister" value="注册">
            </div>
          </form>
          <div class="login-skip">
            已有账号？ <a href="javascript:void(0)" @click="goLink('/page/user/login')">登录</a>
          </div>
        </div>
      </div>
    </div>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from "@/components/common/Header";
import Footer from "@/components/common/Footer";
import {doGet, doPost} from "@/api/httpRequest";
import layx from "vue-layx";
import md5 from 'js-md5';

export default {
  name: "RegisterView",
  components: {
    // eslint-disable-next-line vue/no-unused-components
    Header,
    // eslint-disable-next-line vue/no-unused-components
    Footer
  },
  data() {
    return {
      historyAvgRate: 0.0,
      phone: '',
      phoneErr: '',
      password: '',
      passwordErr: '',
      code: '',
      codeErr: '',
      yzmText: '获取验证码',
      isSend: false,
      agree: false
    }
  },
  mounted() {
    //向服务器发起请求，获取数据，更新页面
    doGet('/v1/plat/info').then(resp => {
      if (resp) {
        this.historyAvgRate = resp.data.data.historyAvgRate;
      }
    })
  },
  methods: {
    goLink(url, params) {
      //使用router做页面跳转， vue中的对象
      this.$router.push({
        path: url,
        query: params
      })
    },
    checkPhone() {
      if (this.phone == '' || this.phone == undefined) {
        this.phoneErr = '请输入手机号';
      } else if (this.phone.length != 11) {
        this.phoneErr = '手机号长度不足11位';
      } else if (!/^1[1-9]\d{9}$/.test(this.phone)) {
        this.phoneErr = '手机号格式不正确'
      } else {
        this.phoneErr = '';
        //向服务器发起请求，验证手机号是否可以注册
        doGet('/v1/user/phone/exists', {phone: this.phone})
            .then(resp => {
              if (resp && resp.data.code == 1000) {
                //手机号可以注册
                console.log("手机号可以注册")
              } else {
                this.phoneErr = resp.data.msg;
              }
            })
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
      //isSend:true, 发送验证码， 倒计时正在执行中。  false可以重新发送验证码
      if (this.isSend == false) {
        this.checkPhone();
        if (this.phoneErr == '') {
          this.isSend = true;
          let btn = document.getElementById("yzmBtn");
          btn.style.color = 'red';
          //处理倒计时
          let second = 5; //倒计时时间，默认是60秒
          setInterval(() => {
            if (second <= 1) {
              btn.style.color = '#688EF9';
              this.yzmText = "获取验证码";
              this.isSend = false;
            } else {
              second = second - 1;
              this.yzmText = second + "秒后重新获取";
            }
          }, 1000)
          //发起请求，给手机号发送验证码
          doGet('/v1/sms/code/register', {phone: this.phone}).then(resp => {
            if (resp && (resp.data.code == 1000 || resp.data.code == 1006)) {
              layx.msg('短信已经发送了', {dialogIcon: 'success', position: 'ct'});
            }
          })
        }
      }
    },
    requestUserRegister() {
      this.checkPhone();
      this.checkPassword();
      this.checkCode();
      if (this.phoneErr == '' && this.passwordErr == '' && this.codeErr == '') {
        if (this.agree) {
          //数据正确，提交注册请求
          //前端密码的md5()
          let newPassword = md5(this.password);
          doPost('/v1/user/register', {
            phone: this.phone,
            pword: newPassword,
            scode: this.code
          }).then(resp => {
            if (resp && resp.data.code == 1000) {
              //跳转登录页面
              this.$router.push({
                path: "/page/user/login"
              })
            }
          })
        } else {
          layx.msg("请阅读注册协议", {dialogIcon: 'warn', position: 'ct'});
        }
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