<template>
  <div class="login">
    <section class="login-hero">
      <div class="brand-lockup">
        <div class="brand-mark">B</div>
        <div>
          <div class="brand-name">Biomass Admin</div>
          <div class="brand-subtitle">生物质管理平台</div>
        </div>
      </div>
      <div class="hero-copy">
        <h1>统一运营、权限与数据管理</h1>
        <p>面向生产环境的现代化后台工作台，保留若依 RBAC 能力并重塑为 SaaS 控制台体验。</p>
      </div>
      <div class="hero-metrics">
        <div v-for="item in metrics" :key="item.label" class="metric-item">
          <span>{{ item.value }}</span>
          <label>{{ item.label }}</label>
        </div>
      </div>
    </section>

    <section class="login-panel">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <div class="form-heading">
          <h2>{{ title }}</h2>
          <p>登录控制台继续管理业务与系统权限</p>
        </div>

        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            type="text"
            auto-complete="off"
            placeholder="请输入账号"
          >
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            auto-complete="off"
            placeholder="请输入密码"
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
          <span>企业内控访问</span>
        </div>

        <el-button
          :loading="loading"
          class="login-submit"
          size="medium"
          type="primary"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登录控制台</span>
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
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      metrics: [
        { value: "RBAC", label: "权限体系" },
        { value: "Vue2", label: "稳定技术栈" },
        { value: "SaaS", label: "现代体验" }
      ],
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
  grid-template-columns: minmax(420px, 1fr) 480px;
  min-height: 100%;
  background:
    radial-gradient(circle at 12% 16%, rgba(22, 119, 255, 0.16), transparent 30%),
    linear-gradient(135deg, #f7faff 0%, #eef4ff 45%, #ffffff 100%);
}

.login-hero {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 56px 72px;
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  color: #fff;
  background: linear-gradient(135deg, #1677ff, #00a3ff);
  box-shadow: 0 14px 28px rgba(22, 119, 255, 0.22);
  font-size: 22px;
  font-weight: 700;
}

.brand-name {
  color: #1f2329;
  font-size: 18px;
  font-weight: 700;
  line-height: 24px;
}

.brand-subtitle {
  color: #646a73;
  font-size: 13px;
  line-height: 20px;
}

.hero-copy {
  max-width: 640px;

  h1 {
    margin: 0 0 20px;
    color: #111827;
    font-size: 44px;
    font-weight: 700;
    line-height: 1.18;
  }

  p {
    margin: 0;
    color: #56606f;
    font-size: 16px;
    line-height: 28px;
  }
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  max-width: 620px;
}

.metric-item {
  padding: 18px;
  border: 1px solid rgba(22, 119, 255, 0.12);
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 10px 30px rgba(31, 35, 41, 0.06);
  backdrop-filter: blur(12px);

  span {
    display: block;
    color: #1677ff;
    font-size: 22px;
    font-weight: 700;
    line-height: 28px;
  }

  label {
    display: block;
    margin-top: 6px;
    color: #646a73;
    font-size: 13px;
    font-weight: 500;
  }
}

.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: -16px 0 48px rgba(31, 35, 41, 0.08);
}

.login-form {
  width: 100%;
  padding: 36px;
  border: 1px solid #edf1f7;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 18px 48px rgba(31, 35, 41, 0.08);

  .el-input {
    height: 44px;

    ::v-deep input {
      height: 44px;
      border-radius: 10px;
    }
  }

  .input-icon {
    width: 15px;
    height: 44px;
    margin-left: 4px;
    color: #8f959e;
  }
}

.form-heading {
  margin-bottom: 28px;

  h2 {
    margin: 0;
    color: #1f2329;
    font-size: 24px;
    font-weight: 700;
    line-height: 32px;
  }

  p {
    margin: 8px 0 0;
    color: #646a73;
    font-size: 14px;
  }
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 116px;
  gap: 12px;
}

.login-code {
  height: 44px;
  padding: 0;
  border: 1px solid #e5e8ef;
  border-radius: 10px;
  background: #f7f9fc;
  cursor: pointer;
  overflow: hidden;
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
  color: #8f959e;
  font-size: 13px;
}

.login-submit {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
}

.el-login-footer {
  margin-top: 22px;
  color: #8f959e;
  text-align: center;
  font-size: 12px;
}

@media (max-width: 960px) {
  .login {
    grid-template-columns: 1fr;
  }

  .login-hero {
    display: none;
  }

  .login-panel {
    min-height: 100vh;
    padding: 24px;
  }

  .login-form {
    padding: 28px;
  }
}
</style>
