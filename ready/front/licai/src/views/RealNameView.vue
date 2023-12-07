<template>
  <div>
    <Header></Header>
    <div class="login-content">
      <div class="login-flex">
        <div class="login-left"></div>
        <!---->
        <div class="login-box">
          <h3 class="login-title">实名认证</h3>
          <form action="" id="renZ_Submit">
            <div class="alert-input">
              <input type="text" class="form-border user-name" name="username" v-model="name" @blur="checkName"
                     placeholder="请输入您的真实姓名">
              <div class="err">{{ nameErr }}</div>
              <p class="prompt_name"></p>
              <input type="text" class="form-border user-sfz" name="sfz" v-model="idCard" @blur="checkIdCard"
                     placeholder="请输入15位或18位身份证号">
              <div class="err">{{ idCardErr }}</div>
              <p class="prompt_sfz"></p>
              <input type="text" class="form-border user-num" name="mobile" v-bind:value="phone" readonly
                     placeholder="请输入11位手机号">
              <p class="prompt_num"></p>
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
              <input type="button" @click="requestRealname" class="login-submit" value="认证">
            </div>
          </form>
          <div class="login-skip">
            暂不认证？ <a href="javascript:;" @click="goLink('/page/user/usercenter')">跳过</a>
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
import {doGet, doPostJson} from "@/api/httpRequest";
import layx from "vue-layx";

export default {
  name: "RealNameView",
  components: {
    // eslint-disable-next-line vue/no-unused-components
    Header,
    // eslint-disable-next-line vue/no-unused-components
    Footer
  },
  data() {
    return {
      name: '',
      nameErr: '',
      idCard: '',
      idCardErr: '',
      code: '',
      codeErr: '',
      phone: '',
      agree: false,
      historyAvgRate: 0.0,
      password: '',
      passwordErr: '',
      yzmText: '获取验证码',
      isSend: false,
    }
  },
  mounted() {
    //获取localStorage中的用户数据
    let userinfo = window.localStorage.getItem("userinfo");
    if (userinfo) {
      this.phone = JSON.parse(userinfo).phone;
    }
  },
  methods: {
    goLink(url, params) {
      //使用router做页面跳转， vue中的对象
      this.$router.push({
        path: url,
        query: params
      })
    },
    checkName() {
      if (this.name == '' || this.name == null) {
        this.nameErr = '必须输入姓名';
      } else if (this.name.length < 2) {
        this.nameErr = '姓名不正确';
      } else if (!/^[\u4e00-\u9fa5]{0,}$/.test(this.name)) {
        this.nameErr = '姓名必须是中文';
      } else {
        this.nameErr = '';
      }
    },
    checkIdCard() {
      if (this.idCard == '' || this.idCard == null) {
        this.idCardErr = '请输入身份证号';
      } else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(this.idCard)) {
        this.idCardErr = '身份证号格式不正确';
      } else {
        this.idCardErr = '';
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
        doGet('/v1/sms/code/realname', {phone: this.phone}).then(resp => {
          if (resp && (resp.data.code == 1000 || resp.data.code == 1006)) {
            layx.msg('短信已经发送了', {dialogIcon: 'success', position: 'ct'});
          }
        })
      }
    },
    requestRealname() {
      if (this.agree == false) {
        layx.msg('请阅读注册协议.', {dialogIcon: 'warn', position: 'ct'});
        return;
      }
      this.checkName();
      this.checkIdCard();
      this.checkCode();
      if (this.codeErr == '' && this.nameErr == '' && this.idCardErr == '') {
        let param = {
          name: this.name,
          phone: this.phone,
          idCard: this.idCard,
          code: this.code
        }
        doPostJson('/v1/user/realname', param).then(resp => {
          if (resp && resp.data.code == 1000) {
            window.localStorage.setItem("userinfo", JSON.stringify(resp.data.data));
            //跳转到用户中心
            this.$router.push({
              path: '/page/user/usercenter'
            })
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