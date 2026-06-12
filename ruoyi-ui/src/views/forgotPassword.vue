<template>
  <div class="forgot-page">
    <section class="forgot-aside">
      <div class="brand-lockup">
        <img :src="logo" class="brand-mark" alt="澳琴空间预约系统">
        <div class="brand-name">澳琴空间预约系统</div>
      </div>
      <div class="aside-copy">
        <h1>重置登录密码</h1>
        <p>通过账号绑定邮箱完成身份验证，安全返回预约管理工作台。</p>
      </div>
      <div class="space-visual" aria-hidden="true">
        <div class="building-card">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div class="calendar-card">
          <strong>安全</strong>
          <em>Account verify</em>
        </div>
        <div class="route-line"></div>
      </div>
    </section>

    <section class="forgot-panel">
      <el-form ref="form" :model="form" :rules="formRules" class="forgot-form">
        <div class="form-brand">
          <img :src="logo" class="form-logo" alt="澳琴空间预约系统">
        </div>
        <div class="form-heading">
          <h2>{{ step === 1 ? '验证绑定邮箱' : '设置新密码' }}</h2>
          <p>{{ step === 1 ? '请输入账号与绑定邮箱，获取邮箱验证码。' : '输入邮箱验证码并设置新的登录密码。' }}</p>
        </div>

        <el-form-item prop="username">
          <el-input v-model.trim="form.username" placeholder="登录账号" :disabled="step === 2">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <el-form-item prop="contact">
          <el-input v-model.trim="form.contact" placeholder="绑定邮箱" :disabled="step === 2">
            <svg-icon slot="prefix" icon-class="email" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>

        <template v-if="step === 1">
          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input v-model.trim="form.code" placeholder="图形验证码" @keyup.enter.native="sendCode">
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <button type="button" class="captcha-button" @click="getCode">
                <img :src="codeUrl" class="captcha-img" alt="图形验证码">
              </button>
            </div>
          </el-form-item>

          <el-button
            :loading="sending"
            class="primary-submit"
            type="primary"
            @click.native.prevent="sendCode"
          >
            {{ sending ? '发送中...' : countdown > 0 ? countdown + '秒后重试' : '发送邮箱验证码' }}
          </el-button>
        </template>

        <template v-else>
          <el-form-item prop="verifyCode">
            <div class="verify-row">
              <el-input v-model.trim="form.verifyCode" placeholder="邮箱验证码">
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <el-button plain :disabled="countdown > 0" @click="backToVerify">
                {{ countdown > 0 ? countdown + '秒' : '重发' }}
              </el-button>
            </div>
          </el-form-item>

          <el-form-item prop="newPassword">
            <el-input v-model="form.newPassword" type="password" placeholder="新密码" show-password>
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认新密码" show-password @keyup.enter.native="submitReset">
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-button
            :loading="submitting"
            class="primary-submit"
            type="primary"
            @click.native.prevent="submitReset"
          >
            {{ submitting ? '提交中...' : '重置密码' }}
          </el-button>
        </template>

        <button type="button" class="back-login" @click="backLogin">返回登录</button>
      </el-form>
    </section>
  </div>
</template>

<script>
import { getCodeImg, getPasswordResetConfig, sendPasswordResetCode, resetPassword } from '@/api/login'
import { validEmail } from '@/utils/validate'
import logo from '@/assets/logo/logo.png'

const PWD_RULES = {
  '0': { pattern: /^[^<>"'|\\]+$/, message: '密码不能包含非法字符：< > " \' \\ |' },
  '1': { pattern: /^[0-9]+$/, message: '密码只能为数字' },
  '2': { pattern: /^[a-zA-Z]+$/, message: '密码只能为英文字母' },
  '3': { pattern: /^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$/, message: '密码必须同时包含字母和数字' },
  '4': { pattern: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[~!@#$%^&*()\-=_+])[A-Za-z\d~!@#$%^&*()\-=_+]+$/, message: '密码必须同时包含字母、数字和特殊字符' }
}

export default {
  name: 'ForgotPassword',
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入绑定邮箱'))
      } else if (!validEmail(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入新密码'))
        return
      }
      if (value.length < this.passwordMinLength || value.length > this.passwordMaxLength) {
        callback(new Error(`密码长度必须在 ${this.passwordMinLength} 到 ${this.passwordMaxLength} 个字符之间`))
        return
      }
      const ruleConfig = PWD_RULES[this.pwdChrtype] || PWD_RULES['0']
      if (!ruleConfig.pattern.test(value)) {
        callback(new Error(ruleConfig.message))
        return
      }
      callback()
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请确认新密码'))
      } else if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      logo,
      step: 1,
      codeUrl: '',
      captchaEnabled: true,
      sending: false,
      submitting: false,
      countdown: 0,
      timer: null,
      passwordMinLength: 5,
      passwordMaxLength: 20,
      pwdChrtype: '0',
      resendIntervalSeconds: 60,
      form: {
        username: '',
        contactType: 'email',
        contact: '',
        code: '',
        uuid: '',
        verifyCode: '',
        newPassword: '',
        confirmPassword: ''
      },
      formRules: {
        username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
        contact: [{ validator: validateEmail, trigger: 'blur' }],
        code: [{ required: true, message: '请输入图形验证码', trigger: 'change' }],
        verifyCode: [{ required: true, message: '请输入邮箱验证码', trigger: 'blur' }],
        newPassword: [{ validator: validatePassword, trigger: 'blur' }],
        confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadConfig()
    this.getCode()
  },
  beforeDestroy() {
    this.clearTimer()
  },
  methods: {
    loadConfig() {
      getPasswordResetConfig().then(res => {
        const data = res.data || {}
        this.passwordMinLength = data.passwordMinLength || this.passwordMinLength
        this.passwordMaxLength = data.passwordMaxLength || this.passwordMaxLength
        this.pwdChrtype = data.pwdChrtype || this.pwdChrtype
        this.resendIntervalSeconds = data.resendIntervalSeconds || this.resendIntervalSeconds
      })
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.form.uuid = res.uuid
        }
      })
    },
    sendCode() {
      if (this.countdown > 0) return
      this.$refs.form.validate(valid => {
        if (valid) {
          this.doSendCode()
        }
      })
    },
    backToVerify() {
      this.step = 1
      this.form.code = ''
      this.form.verifyCode = ''
      if (this.captchaEnabled) {
        this.getCode()
      }
    },
    doSendCode() {
      this.sending = true
      sendPasswordResetCode({
        username: this.form.username,
        contactType: this.form.contactType,
        contact: this.form.contact,
        code: this.form.code,
        uuid: this.form.uuid
      }).then(res => {
        this.$modal.msgSuccess(res.msg || '验证码已发送')
        this.step = 2
        this.form.code = ''
        this.startCountdown()
        if (this.captchaEnabled) {
          this.getCode()
        }
      }).catch(() => {
        if (this.captchaEnabled) {
          this.getCode()
        }
      }).finally(() => {
        this.sending = false
      })
    },
    submitReset() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        resetPassword({
          username: this.form.username,
          contactType: this.form.contactType,
          contact: this.form.contact,
          verifyCode: this.form.verifyCode,
          newPassword: this.form.newPassword
        }).then(res => {
          this.$modal.msgSuccess(res.msg || '密码重置成功')
          this.$router.push({ path: '/login' })
        }).finally(() => {
          this.submitting = false
        })
      })
    },
    startCountdown() {
      this.clearTimer()
      this.countdown = this.resendIntervalSeconds
      this.timer = setInterval(() => {
        this.countdown -= 1
        if (this.countdown <= 0) {
          this.clearTimer()
        }
      }, 1000)
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
      if (this.countdown < 0) {
        this.countdown = 0
      }
    },
    backLogin() {
      this.$router.push({ path: '/login' })
    }
  }
}
</script>

<style lang="scss" scoped>
.forgot-page {
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

.forgot-aside {
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

.forgot-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 42px 52px;
  background: rgba(248, 251, 255, .76);
  overflow-x: hidden;
}

.forgot-form {
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

.captcha-row,
.verify-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 116px;
  gap: 10px;

  > * {
    min-width: 0;
  }
}

.captcha-button {
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

.captcha-img {
  display: block;
  width: 100%;
  height: 44px;
  object-fit: cover;
}

.primary-submit {
  width: 100%;
  height: 44px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #1687c7 0%, #18a999 100%);
  box-shadow: 0 14px 26px rgba(22, 135, 199, .24);
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

.back-login {
  display: block;
  margin: 18px auto 0;
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

@media (max-width: 960px) {
  .forgot-page {
    grid-template-columns: 1fr;
  }

  .forgot-aside {
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
  }

  .space-visual {
    display: none;
  }

  .forgot-panel {
    align-items: stretch;
    min-height: auto;
    padding: 28px 24px 34px;
  }

  .forgot-form {
    width: 100%;
    max-width: 420px;
    margin: 0 auto;
    padding: 28px;
  }
}

@media (max-width: 420px) {
  .forgot-aside {
    padding: 24px 18px 20px;
  }

  .forgot-panel {
    padding: 18px 14px;
  }

  .forgot-form {
    width: 100%;
    max-width: 100%;
    padding: 24px 20px;
  }

  .captcha-row,
  .verify-row {
    grid-template-columns: minmax(0, 1fr) 78px;
    gap: 8px;
  }
}
</style>
