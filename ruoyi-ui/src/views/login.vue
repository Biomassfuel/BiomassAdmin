<template>
  <div class="login">
    <section class="login-aside">
      <div class="brand-lockup">
        <img :src="logo" class="brand-mark" alt="BiomassAdmin">
        <div class="brand-name">BiomassAdmin</div>
      </div>
      <div class="aside-copy">
        <h1>BiomassAdmin</h1>
        <p>面向生物质能源场景的后台管理系统。</p>
      </div>
    </section>

    <section class="login-panel">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-brand">
          <img :src="logo" class="form-logo" alt="BiomassAdmin">
        </div>
        <div class="form-heading">
          <h2>{{ title }}</h2>
          <p>欢迎回来，请登录工作台。</p>
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
          <span>仅限授权用户</span>
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
      title: 'BiomassAdmin',
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
    }
  }
}
</script>

<style lang="scss" scoped>
.login {
  position: relative;
  display: grid;
  grid-template-columns: minmax(520px, 1fr) minmax(430px, 520px);
  width: 100%;
  min-height: 100%;
  background:
    linear-gradient(118deg, rgba(210, 232, 190, .56) 0 28%, transparent 28% 100%),
    linear-gradient(144deg, transparent 0 58%, rgba(220, 238, 205, .64) 58% 100%),
    linear-gradient(135deg, #f7fbf3 0%, #eef7ee 48%, #f6f8fb 100%);
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
  border-right: 1px solid rgba(58, 125, 68, .12);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .88), rgba(255, 255, 255, .52)),
    linear-gradient(155deg, rgba(224, 242, 204, .92), rgba(238, 247, 229, .74) 48%, rgba(241, 246, 240, .88));
  color: #153426;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -80px;
    bottom: 86px;
    width: 420px;
    height: 170px;
    border: 1px solid rgba(50, 91, 64, .08);
    border-radius: 28px;
    transform: rotate(-18deg);
  }

  &::after {
    content: '';
    position: absolute;
    left: 78px;
    bottom: 140px;
    width: 320px;
    height: 1px;
    background: linear-gradient(90deg, rgba(63, 143, 77, .38), transparent);
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
  color: #17372a;
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
    color: #10291f;
    font-size: 48px;
    font-weight: 760;
    line-height: 1.12;
  }

  p {
    margin: 0;
    max-width: 460px;
    color: #52655b;
    font-size: 16px;
    line-height: 30px;
  }
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
  background: rgba(248, 250, 247, .72);
  overflow-x: hidden;
}

.login-form {
  box-sizing: border-box !important;
  width: 100%;
  max-width: 398px;
  min-width: 0;
  padding: 36px;
  border: 1px solid rgba(58, 125, 68, .11);
  border-radius: 8px;
  background: rgba(255, 255, 255, .94);
  box-shadow: 0 22px 60px rgba(31, 64, 47, .12);

  .el-input {
    width: 100%;
    min-width: 0;
    height: 44px;

    ::v-deep input {
      height: 44px;
      border-color: #dce6d7;
      border-radius: 8px;
      color: #20342b;
      background: #fbfdf9;

      &:focus {
        border-color: #6da447;
        box-shadow: 0 0 0 3px rgba(109, 164, 71, .14);
      }

      &::placeholder {
        color: #93a29a;
      }
    }
  }

  .input-icon {
    width: 15px;
    height: 44px;
    margin-left: 4px;
    color: #8da094;
  }

  ::v-deep .el-form-item__content {
    min-width: 0;
  }

  ::v-deep .el-form-item {
    margin-bottom: 20px;
  }

  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
    border-color: #3f8f4d;
    background-color: #3f8f4d;
  }

  ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #315640;
  }
}

.form-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  margin-bottom: 18px;
  border: 1px solid rgba(58, 125, 68, .1);
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 24px rgba(46, 88, 56, .12);
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
    color: #12251c;
    font-size: 26px;
    font-weight: 760;
    line-height: 32px;
  }

  p {
    margin: 8px 0 0;
    color: #6d7b72;
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
  border: 1px solid #dce6d7;
  border-radius: 8px;
  background: #fbfdf9;
  cursor: pointer;
  overflow: hidden;
  transition: border-color .18s ease, box-shadow .18s ease;

  &:hover {
    border-color: #8ab46a;
    box-shadow: 0 0 0 3px rgba(109, 164, 71, .1);
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
  color: #58675f;
  font-size: 13px;
}

.login-submit {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #3f8f4d 0%, #77a835 100%);
  box-shadow: 0 14px 26px rgba(63, 143, 77, .24);
  font-size: 14px;
  font-weight: 600;
  transition: transform .18s ease, box-shadow .18s ease, opacity .18s ease;

  &:hover,
  &:focus {
    background: linear-gradient(135deg, #367f44 0%, #6c9f2f 100%);
    box-shadow: 0 16px 30px rgba(63, 143, 77, .28);
    transform: translateY(-1px);
  }

  &.is-loading {
    transform: none;
  }
}

.el-login-footer {
  margin-top: 24px;
  color: #7a887f;
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
      font-size: 30px;
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
