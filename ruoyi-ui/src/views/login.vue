<template>
  <div class="login">
    <section class="login-aside">
      <div class="brand-lockup">
        <div class="brand-mark">B</div>
        <div class="brand-name">BiomassAdmin</div>
      </div>
      <div class="aside-copy">
        <h1>BiomassAdmin</h1>
        <p>清晰处理权限、配置、日志与开发工具。</p>
      </div>
    </section>

    <section class="login-panel">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-heading">
          <h2>{{ title }}</h2>
          <p>欢迎回来，请登录。</p>
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

export default {
  name: "Login",
  data() {
    return {
      title: 'BiomassAdmin',
      footerContent: defaultSettings.footerContent,
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
  display: grid;
  grid-template-columns: minmax(420px, 1fr) 460px;
  min-height: 100%;
  background: #f3f4f6;
}

.login-aside {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  padding: 52px 68px;
  background: #111827;
  color: #ffffff;
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 9px;
  background: #1f2937;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.brand-name {
  color: #ffffff;
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.aside-copy {
  max-width: 520px;

  h1 {
    margin: 0 0 16px;
    color: #ffffff;
    font-size: 42px;
    font-weight: 700;
    line-height: 1.18;
  }

  p {
    margin: 0;
    color: #aeb8c6;
    font-size: 15px;
    line-height: 26px;
  }
}

.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  background: #f9fafb;
}

.login-form {
  width: 100%;
  max-width: 380px;
  padding: 32px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);

  .el-input {
    height: 42px;

    ::v-deep input {
      height: 42px;
      border-radius: 8px;
    }
  }

  .input-icon {
    width: 15px;
    height: 42px;
    margin-left: 4px;
    color: #9ca3af;
  }
}

.form-heading {
  margin-bottom: 26px;

  h2 {
    margin: 0;
    color: #111827;
    font-size: 24px;
    font-weight: 700;
    line-height: 32px;
  }

  p {
    margin: 6px 0 0;
    color: #6b7280;
    font-size: 13px;
  }
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 112px;
  gap: 10px;
}

.login-code {
  height: 42px;
  padding: 0;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f3f4f6;
  cursor: pointer;
  overflow: hidden;
}

.login-code-img {
  display: block;
  width: 100%;
  height: 42px;
  object-fit: cover;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 2px 0 22px;
  color: #6b7280;
  font-size: 13px;
}

.login-submit {
  width: 100%;
  height: 42px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
}

.el-login-footer {
  margin-top: 22px;
  color: #9ca3af;
  text-align: center;
  font-size: 12px;
}

@media (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
  }

  .login-aside {
    display: none;
  }

  .login-panel {
    align-items: center;
    overflow-x: hidden;
    min-height: 100vh;
    padding: 24px;
  }

  .login-form {
    width: calc(100vw - 48px);
    max-width: 360px;
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
  .login-panel {
    padding: 18px;
  }

  .login-form {
    width: calc(100vw - 36px);
    max-width: 100%;
    padding: 24px;
  }

  .captcha-row {
    grid-template-columns: minmax(0, 1fr) 92px;
    gap: 8px;
  }

  .login-options {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
