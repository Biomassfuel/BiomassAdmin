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
  grid-template-columns: minmax(460px, 1fr) 480px;
  width: 100%;
  min-height: 100%;
  background: #f3f4f6;
  overflow-x: hidden;
}

.login-aside {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100vh;
  padding: 54px 72px;
  border-right: 1px solid rgba(148, 163, 184, .14);
  background:
    linear-gradient(135deg, rgba(37, 99, 235, .28), transparent 38%),
    linear-gradient(180deg, #111827 0%, #0f172a 100%);
  color: #f8fafc;
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
  border-radius: 8px;
  background: linear-gradient(135deg, #2563eb 0%, #10b981 100%);
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.24);
}

.brand-name {
  color: #f8fafc;
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.aside-copy {
  max-width: 520px;

  h1 {
    margin: 0 0 16px;
    color: #f8fafc;
    font-size: 44px;
    font-weight: 750;
    line-height: 1.18;
  }

  p {
    margin: 0;
    max-width: 420px;
    color: #cbd5e1;
    font-size: 15px;
    line-height: 28px;
  }
}

.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-width: 0;
  padding: 42px;
  background: #f3f4f6;
  overflow-x: hidden;
}

.login-form {
  width: 100%;
  max-width: 386px;
  min-width: 0;
  padding: 34px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0 12px 34px rgba(15, 23, 42, 0.08);

  .el-input {
    width: 100%;
    min-width: 0;
    height: 42px;

    ::v-deep input {
      height: 42px;
      border-radius: 7px;
    }
  }

  .input-icon {
    width: 15px;
    height: 42px;
    margin-left: 4px;
    color: #9ca3af;
  }

  ::v-deep .el-form-item__content {
    min-width: 0;
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

  > * {
    min-width: 0;
  }
}

.login-code {
  width: 100%;
  min-width: 0;
  height: 42px;
  padding: 0;
  border: 1px solid #d7dde7;
  border-radius: 7px;
  background: #f9fafb;
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
  color: #4b5563;
  font-size: 13px;
}

.login-submit {
  width: 100%;
  height: 42px;
  border-radius: 7px;
  font-size: 14px;
  font-weight: 600;
}

.el-login-footer {
  margin-top: 22px;
  color: #6b7280;
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
    align-items: stretch;
    overflow-x: hidden;
    min-height: 100vh;
    padding: 24px;
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
  .login-panel {
    align-items: stretch;
    padding: 18px;
  }

  .login-form {
    width: 100%;
    max-width: 100%;
    padding: 24px;
  }

  .captcha-row {
    grid-template-columns: minmax(0, 1fr) 82px;
    gap: 8px;
  }

  .login-options {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }
}
</style>
