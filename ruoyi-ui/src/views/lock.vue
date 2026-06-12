<template>
  <div class="lock-container">
    <!-- 动态粒子背景 -->
    <canvas ref="particleCanvas" class="particle-bg"></canvas>

    <section class="lock-shell">
      <div class="lock-time-panel">
        <div class="brand-lockup">
          <img :src="logo" class="brand-mark" alt="澳琴空间预约系统">
          <span class="brand-name">澳琴空间预约系统</span>
        </div>
        <div class="lock-time">{{ currentTime }}</div>
        <div class="lock-date">{{ currentDate }}</div>
      </div>

      <div class="lock-card">
        <div class="lock-icon-wrap">
          <div class="lock-icon"><i class="el-icon-lock"></i></div>
        </div>
        <div class="lock-username">{{ nickName }}</div>
        <div class="lock-hint">系统已锁定，请输入密码解锁</div>

        <div class="input-wrap" :class="{ shake: isShaking }">
          <input ref="passwordInput" v-model="password" type="password" placeholder="请输入登录密码" class="lock-input" @keydown.enter="handleUnlock" autocomplete="off" />
          <button class="unlock-btn" @click="handleUnlock" :disabled="loading">
            <i v-if="!loading" class="el-icon-right"></i>
            <span v-else class="loading-dot">···</span>
          </button>
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

        <div class="lock-footer">
          <a href="/login" @click.prevent="goLogin">退出重新登录</a>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { unlockScreen } from '@/api/login'
import logo from '@/assets/logo/logo.png'

export default {
  name: 'LockScreen',
  data() {
    return {
      password: '',
      loading: false,
      errorMsg: '',
      isShaking: false,
      currentTime: '',
      currentDate: '',
      timer: null,
      animationId: null,
      particles: [],
      logo
    }
  },
  computed: {
    ...mapGetters(['nickName'])
  },
  mounted() {
    this.startClock()
    this.initParticles()
    this.$nextTick(() => {
      this.$refs.passwordInput && this.$refs.passwordInput.focus()
    })
  },
  beforeDestroy() {
    clearInterval(this.timer)
    cancelAnimationFrame(this.animationId)
  },
  methods: {
    startClock() {
      const update = () => {
        const now = new Date()
        const h = String(now.getHours()).padStart(2, '0')
        const m = String(now.getMinutes()).padStart(2, '0')
        const s = String(now.getSeconds()).padStart(2, '0')
        this.currentTime = `${h}:${m}:${s}`
        const days = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
        this.currentDate = `${now.getFullYear()}年${now.getMonth()+1}月${now.getDate()}日 ${days[now.getDay()]}`
      }
      update()
      this.timer = setInterval(update, 1000)
    },
    async handleUnlock() {
      if (!this.password) {
        this.showError('请输入密码')
        return
      }
      this.loading = true
      this.errorMsg = ''
      try {
        await unlockScreen(this.password)
        const lockPath = this.$store.getters.lockPath  // 取锁屏前的路径
        await this.$store.dispatch('lock/unlockScreen')
        this.$router.replace(lockPath)
      } catch (err) {
        this.showError(this.normalizeUnlockError(err))
        this.password = ''
        this.$refs.passwordInput && this.$refs.passwordInput.focus()
      } finally {
        this.loading = false
      }
    },
    normalizeUnlockError(err) {
      const msg = String((err && err.message) || err || '')
      if (msg.includes('No static resource') || msg.includes('404')) {
        return '解锁接口不可用，请刷新后重试'
      }
      if (msg.includes('401') || msg.includes('认证失败') || msg.includes('会话')) {
        return '登录状态已过期，请重新登录'
      }
      if (msg.includes('密码')) {
        return msg
      }
      return msg || '解锁失败，请稍后重试'
    },
    showError(msg) {
      this.errorMsg = msg
      this.isShaking = true
      setTimeout(() => { this.isShaking = false }, 600)
    },
    goLogin() {
      this.$store.dispatch('lock/unlockScreen')
      this.$store.dispatch('LogOut').then(() => {
        this.$router.push('/login')
      })
    },
    // 粒子背景
    initParticles() {
      const canvas = this.$refs.particleCanvas
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const resize = () => {
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
      }
      resize()
      window.addEventListener('resize', resize)
      const count = 80
      for (let i = 0; i < count; i++) {
        this.particles.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          r: Math.random() * 2 + 1,
          dx: (Math.random() - 0.5) * 0.6,
          dy: (Math.random() - 0.5) * 0.6,
          alpha: Math.random() * 0.5 + 0.2
        })
      }
      const draw = () => {
        ctx.clearRect(0, 0, canvas.width, canvas.height)
        this.particles.forEach(p => {
          ctx.beginPath()
          ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
          ctx.fillStyle = `rgba(63,143,77,${p.alpha * 0.28})`
          ctx.fill()
          p.x += p.dx
          p.y += p.dy
          if (p.x < 0 || p.x > canvas.width) p.dx *= -1
          if (p.y < 0 || p.y > canvas.height) p.dy *= -1
        })
        // 连线
        for (let i = 0; i < this.particles.length; i++) {
          for (let j = i + 1; j < this.particles.length; j++) {
            const a = this.particles[i], b = this.particles[j]
            const dist = Math.hypot(a.x - b.x, a.y - b.y)
            if (dist < 120) {
              ctx.beginPath()
              ctx.moveTo(a.x, a.y)
              ctx.lineTo(b.x, b.y)
              ctx.strokeStyle = `rgba(63,143,77,${0.08 * (1 - dist / 120)})`
              ctx.lineWidth = 0.5
              ctx.stroke()
            }
          }
        }
        this.animationId = requestAnimationFrame(draw)
      }
      draw()
    }
  }
}
</script>

<style scoped>
.lock-container {
  position: fixed;
  inset: 0;
  background:
    linear-gradient(118deg, rgba(210, 232, 190, .56) 0 28%, transparent 28% 100%),
    linear-gradient(144deg, transparent 0 58%, rgba(220, 238, 205, .58) 58% 100%),
    linear-gradient(135deg, #f7fbf3 0%, #eef7ee 48%, #f6f8fb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 42px;
  font-family: Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
  overflow: hidden;
}

.lock-container *,
.lock-container *::before,
.lock-container *::after {
  box-sizing: border-box;
}

.particle-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  opacity: .82;
}

.lock-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(360px, 1fr) 380px;
  gap: 28px;
  align-items: stretch;
  width: min(940px, 100%);
}

.lock-time-panel,
.lock-card {
  border: 1px solid rgba(58, 125, 68, .11);
  border-radius: 8px;
  background: rgba(255, 255, 255, .94);
  box-shadow: 0 22px 60px rgba(31, 64, 47, .12);
  backdrop-filter: blur(10px);
}

.lock-time-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 420px;
  padding: 34px;
  overflow: hidden;
}

.lock-time-panel::before {
  content: '';
  position: absolute;
  right: -96px;
  bottom: 54px;
  width: 330px;
  height: 132px;
  border: 1px solid rgba(50, 91, 64, .08);
  border-radius: 24px;
  transform: rotate(-18deg);
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 12px;
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

.lock-time {
  position: relative;
  font-size: 72px;
  font-weight: 750;
  color: #10291f;
  letter-spacing: 0;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.lock-date {
  position: relative;
  margin-top: 16px;
  color: #52655b;
  font-size: 15px;
  line-height: 24px;
}

.lock-card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 420px;
  padding: 38px 36px;
}

.lock-icon-wrap {
  position: relative;
  margin-bottom: 16px;
}

.lock-icon {
  position: static;
  background: #eff7e8;
  color: #3f8f4d;
  border-radius: 50%;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: 0 4px 10px rgba(63, 143, 77, .18);
}

.lock-username {
  color: #12251c;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
}

.lock-hint {
  color: #6d7b72;
  font-size: 13px;
  margin-bottom: 28px;
}

.input-wrap {
  width: 100%;
  display: flex;
  align-items: center;
  background: #fbfdf9;
  border: 1px solid #dce6d7;
  border-radius: 8px;
  padding: 4px 4px 4px 20px;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.input-wrap:focus-within {
  border-color: #6da447;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(109, 164, 71, .14);
}

.input-wrap.shake {
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20% { transform: translateX(-8px); }
  40% { transform: translateX(8px); }
  60% { transform: translateX(-6px); }
  80% { transform: translateX(6px); }
}

.lock-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #20342b;
  font-size: 15px;
  padding: 10px 0;
}

.lock-input::placeholder {
  color: #93a29a;
}

.unlock-btn {
  width: 42px;
  height: 42px;
  border-radius: 7px;
  background: linear-gradient(135deg, #3f8f4d 0%, #77a835 100%);
  border: none;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(63, 143, 77, .2);
  transition: transform 0.2s, opacity 0.2s, box-shadow 0.2s, background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.unlock-btn:hover:not(:disabled) {
  transform: scale(1.08);
  background: linear-gradient(135deg, #367f44 0%, #6c9f2f 100%);
  box-shadow: 0 12px 24px rgba(63, 143, 77, .26);
}

.unlock-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-dot {
  font-size: 13px;
  letter-spacing: 1px;
}

.error-msg {
  margin-top: 14px;
  color: #dc2626;
  font-size: 13px;
  text-align: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}

.lock-footer {
  margin-top: 24px;
}

.lock-footer a {
  color: #58675f;
  font-size: 13px;
  text-decoration: none;
  transition: color 0.2s;
}

.lock-footer a:hover {
  color: #3f8f4d;
}

@media (max-width: 820px) {
  .lock-container {
    padding: 22px;
  }

  .lock-shell {
    grid-template-columns: 1fr;
  }

  .lock-time-panel {
    min-height: 220px;
  }

  .lock-time {
    font-size: 52px;
  }
}

@media (max-width: 460px) {
  .lock-container {
    padding: 16px;
  }

  .lock-time-panel,
  .lock-card {
    padding: 26px 22px;
  }

  .lock-time {
    font-size: 42px;
  }
}
</style>
