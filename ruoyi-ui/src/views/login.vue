<template>
  <div class="login">
    <section class="login-aside">
      <div class="brand-lockup">
        <img :src="logo" class="brand-mark" alt="澳琴空间预约系统">
        <div class="brand-name">澳琴空间预约系统</div>
      </div>
      <div class="aside-copy">
        <h1>澳琴空间预约系统</h1>
        <p>面向澳琴国际教育大学城的空间预约、审核与占用查看工作台。</p>
      </div>
      <div class="space-visual" aria-hidden="true">
        <div class="building-card">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div class="calendar-card">
          <strong>09:00</strong>
          <em>Room A-301</em>
        </div>
        <div class="route-line"></div>
      </div>
    </section>

    <section class="login-panel">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-brand">
          <img :src="logo" class="form-logo" alt="澳琴空间预约系统">
        </div>
        <div class="form-heading">
          <h2>{{ title }}</h2>
          <p>欢迎回来，请登录预约管理工作台。</p>
        </div>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="账号"
          >
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="密码"
            show-password
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <el-form-item prop="code" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <button type="button" class="login-code" @click="getCode">
              <img :src="codeUrl" class="login-code-img" alt="验证码" />
            </button>
          </div>
        </el-form-item>

        <div class="login-options">
          <el-checkbox v-model="loginForm.rememberMe">记住登录信息</el-checkbox>
          <button type="button" class="forgot-link" @click="goForgotPassword">忘记密码</button>
        </div>

        <el-button
          :loading="loading"
          class="login-submit"
          size="medium"
          type="primary"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登录</span>
          <span v-else>登录中...</span>
        </el-button>
      </el-form>

      <div class="el-login-footer">
        <span>{{ footerContent }}</span>
      </div>
    </section>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import defaultSettings from '@/settings'
import logo from '@/assets/logo/logo.png'

export default {
  name: "Login",
  data() {
    return {
      title: '澳琴空间预约系统',
      footerContent: defaultSettings.footerContent,
      logo,
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
        code: "",
        uuid: this.loginForm.uuid
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    },
    goForgotPassword() {
      this.$router.push({ path: '/forgot-password' })
    }
  }
}
</script>

<style lang="scss" scoped>
.login {
  position: relative;
  display: grid;
  grid-template-columns: minmax(560px, 1fr) minmax(430px, 520px);
  width: 100%;
  min-height: 100%;
  background:
    radial-gradient(circle at 18% 18%, rgba(29, 164, 216, .12), transparent 30%),
    radial-gradient(circle at 74% 82%, rgba(22, 163, 116, .10), transparent 32%),
    linear-gradient(135deg, #ffffff 0%, #f5fbff 48%, #f4fbf8 100%);
  overflow-x: hidden;

  *,
  *::before,
  *::after {
    box-sizing: border-box;
  }
}

.login-aside {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  padding: 58px 76px 64px;
  border-right: 1px solid rgba(23, 92, 140, .10);
  background:
    linear-gradient(142deg, rgba(255, 255, 255, .96), rgba(255, 255, 255, .70)),
    linear-gradient(155deg, rgba(229, 246, 255, .90), rgba(239, 250, 246, .78) 50%, rgba(248, 251, 255, .92));
  color: #123044;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -96px;
    top: 118px;
    width: 360px;
    height: 360px;
    border: 1px solid rgba(22, 119, 255, .08);
    border-radius: 50%;
    background: radial-gradient(circle, rgba(30, 136, 229, .08), transparent 64%);
  }

  &::after {
    content: '';
    position: absolute;
    left: 76px;
    bottom: 168px;
    width: 360px;
    height: 1px;
    background: linear-gradient(90deg, rgba(22, 119, 255, .34), rgba(20, 184, 166, .20), transparent);
  }
}

.brand-lockup {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  display: block;
  width: 46px;
  height: 46px;
  border-radius: 12px;
  object-fit: contain;
}

.brand-name {
  color: #123044;
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.aside-copy {
  position: relative;
  z-index: 1;
  max-width: 520px;
}

.aside-copy {
  h1 {
    margin: 0 0 18px;
    color: #0f2537;
    font-size: 46px;
    font-weight: 760;
    line-height: 1.16;
  }

  p {
    margin: 0;
    max-width: 500px;
    color: #526778;
    font-size: 16px;
    line-height: 30px;
  }
}

.space-visual {
  position: absolute;
  right: 74px;
  bottom: 86px;
  width: 360px;
  height: 230px;
  pointer-events: none;
}

.building-card,
.calendar-card {
  position: absolute;
  border: 1px solid rgba(22, 119, 255, .12);
  border-radius: 8px;
  background: rgba(255, 255, 255, .82);
  box-shadow: 0 22px 54px rgba(21, 62, 95, .10);
  backdrop-filter: blur(10px);
}

.building-card {
  left: 0;
  bottom: 12px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  width: 230px;
  height: 132px;
  padding: 24px;
  transform: rotate(-7deg);

  span {
    border-radius: 7px;
    background: linear-gradient(180deg, rgba(29, 164, 216, .18), rgba(20, 184, 166, .16));
  }
}

.calendar-card {
  right: 8px;
  top: 12px;
  width: 154px;
  padding: 18px 20px;
  color: #123044;

  strong,
  em {
    display: block;
    font-style: normal;
  }

  strong {
    font-size: 24px;
    font-weight: 750;
    line-height: 30px;
  }

  em {
    margin-top: 6px;
    color: #5a7182;
    font-size: 12px;
    line-height: 18px;
  }
}

.route-line {
  position: absolute;
  right: 118px;
  bottom: 68px;
  width: 132px;
  height: 74px;
  border-right: 2px solid rgba(20, 184, 166, .34);
  border-bottom: 2px solid rgba(20, 184, 166, .34);
  border-radius: 0 0 18px 0;
}

.login-panel {
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-width: 0;
  min-height: 100vh;
  padding: 42px 52px;
  background: rgba(248, 251, 255, .76);
  overflow-x: hidden;
}

.login-form {
  box-sizing: border-box !important;
  width: 100%;
  max-width: 398px;
  min-width: 0;
  padding: 36px;
  border: 1px solid rgba(23, 92, 140, .11);
  border-radius: 8px;
  background: rgba(255, 255, 255, .96);
  box-shadow: 0 24px 68px rgba(21, 62, 95, .12);

  .el-input {
    width: 100%;
    min-width: 0;
    height: 44px;

    ::v-deep input {
      height: 44px;
      border-color: #d9e6ee;
      border-radius: 8px;
      color: #173043;
      background: #fbfdff;

      &:focus {
        border-color: #1d8ed6;
        box-shadow: 0 0 0 3px rgba(29, 142, 214, .14);
      }

      &::placeholder {
        color: #93a6b3;
      }
    }
  }

  .input-icon {
    width: 15px;
    height: 44px;
    margin-left: 4px;
    color: #879ead;
  }

  ::v-deep .el-form-item__content {
    min-width: 0;
  }

  ::v-deep .el-form-item {
    margin-bottom: 20px;
  }

  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
    border-color: #1687c7;
    background-color: #1687c7;
  }

  ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #17445f;
  }
}

.form-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  margin-bottom: 18px;
  border: 1px solid rgba(23, 92, 140, .10);
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 24px rgba(21, 62, 95, .12);
}

.form-logo {
  display: block;
  width: 46px;
  height: 46px;
  object-fit: contain;
}

.form-heading {
  margin-bottom: 28px;

  h2 {
    margin: 0;
    color: #10283b;
    font-size: 25px;
    font-weight: 760;
    line-height: 34px;
  }

  p {
    margin: 8px 0 0;
    color: #677b89;
    font-size: 13px;
    line-height: 20px;
  }
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 116px;
  gap: 10px;

  > * {
    min-width: 0;
  }
}

.login-code {
  width: 100%;
  min-width: 0;
  height: 44px;
  padding: 0;
  border: 1px solid #d9e6ee;
  border-radius: 8px;
  background: #fbfdff;
  cursor: pointer;
  overflow: hidden;
  transition: border-color .18s ease, box-shadow .18s ease;

  &:hover {
    border-color: #66b5df;
    box-shadow: 0 0 0 3px rgba(29, 142, 214, .10);
  }
}

.login-code-img {
  display: block;
  width: 100%;
  height: 44px;
  object-fit: cover;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 22px;
  color: #586b78;
  font-size: 13px;
}

.forgot-link {
  padding: 0;
  border: none;
  color: #1687c7;
  background: transparent;
  font-size: 13px;
  line-height: 20px;
  cursor: pointer;

  &:hover,
  &:focus {
    color: #0f6fa7;
    text-decoration: underline;
  }
}

.login-submit {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #1687c7 0%, #18a999 100%);
  box-shadow: 0 14px 26px rgba(22, 135, 199, .24);
  font-size: 14px;
  font-weight: 600;
  transition: transform .18s ease, box-shadow .18s ease, opacity .18s ease;

  &:hover,
  &:focus {
    background: linear-gradient(135deg, #0f78b4 0%, #129887 100%);
    box-shadow: 0 16px 30px rgba(22, 135, 199, .28);
    transform: translateY(-1px);
  }

  &.is-loading {
    transform: none;
  }
}

.el-login-footer {
  margin-top: 24px;
  color: #788b98;
  text-align: center;
  font-size: 12px;
  line-height: 20px;
}

@media (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
  }

  .login-aside {
    min-height: auto;
    padding: 30px 28px 24px;

    &::before,
    &::after {
      display: none;
    }
  }

  .aside-copy {
    margin-top: 40px;

    h1 {
      font-size: 34px;
    }

    p {
      max-width: 560px;
    }
  }

  .space-visual {
    display: none;
  }

  .login-panel {
    align-items: stretch;
    overflow-x: hidden;
    min-height: auto;
    padding: 28px 24px 34px;
  }

  .login-form {
    width: 100%;
    max-width: 420px;
    margin: 0 auto;
    padding: 28px;
  }

  .captcha-row {
    grid-template-columns: minmax(0, 1fr) 96px;
  }

  .login-options {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 420px) {
  .login-aside {
    padding: 24px 18px 20px;
  }

  .brand-mark {
    width: 42px;
    height: 42px;
  }

  .aside-copy {
    margin-top: 30px;

    h1 {
      font-size: 28px;
    }
  }

  .login-panel {
    align-items: stretch;
    padding: 18px 14px;
  }

  .login-form {
    width: 100%;
    max-width: 100%;
    padding: 24px 20px;
  }

  .captcha-row {
    grid-template-columns: minmax(0, 1fr) 72px;
    gap: 8px;
  }

  .login-options {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
