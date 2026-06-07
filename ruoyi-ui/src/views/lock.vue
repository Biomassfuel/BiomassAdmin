<template>
  <div class="lock-container">
    <!-- 动态粒子背景 -->
    <canvas ref="particleCanvas" class="particle-bg"></canvas>

    <section class="lock-shell">
      <div class="lock-time-panel">
        <div class="brand-lockup">
          <span class="brand-mark">B</span>
          <span class="brand-name">BiomassAdmin</span>
        </div>
        <div class="lock-time">{{ currentTime }}</div>
        <div class="lock-date">{{ currentDate }}</div>
      </div>

      <div class="lock-card">
        <div class="avatar-wrap">
          <img :src="avatar" class="lock-avatar" @error="onAvatarError" />
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
import defAva from '@/assets/images/profile.jpg'

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
      particles: []
    }
  },
  computed: {
    ...mapGetters(['avatar', 'nickName'])
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
    onAvatarError(e) {
      e.target.src = defAva
    },
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
          ctx.fillStyle = `rgba(37,99,235,${p.alpha * 0.30})`
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
              ctx.strokeStyle = `rgba(37,99,235,${0.08 * (1 - dist / 120)})`
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
    linear-gradient(135deg, rgba(37, 99, 235, 0.10), transparent 34%),
    linear-gradient(180deg, #f9fafb 0%, #f3f4f6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 42px;
  font-family: Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
  overflow: hidden;
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
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: rgba(255, 255, 255, .92);
  box-shadow: 0 12px 34px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
}

.lock-time-panel {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 420px;
  padding: 34px;
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
  font-weight: 750;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.22);
}

.brand-name {
  color: #111827;
  font-size: 17px;
  font-weight: 700;
  line-height: 24px;
}

.lock-time {
  position: relative;
  font-size: 72px;
  font-weight: 750;
  color: #111827;
  letter-spacing: 0;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}

.lock-date {
  position: relative;
  margin-top: 16px;
  color: #4b5563;
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

.avatar-wrap {
  position: relative;
  margin-bottom: 16px;
}

.lock-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #ffffff;
  object-fit: cover;
  display: block;
  box-shadow: 0 10px 24px rgba(29, 33, 41, .12);
}

.lock-icon {
  position: absolute;
  bottom: -4px;
  right: -4px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 50%;
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  box-shadow: 0 4px 10px rgba(37, 99, 235, .16);
}

.lock-username {
  color: #111827;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
}

.lock-hint {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 28px;
}

.input-wrap {
  width: 100%;
  display: flex;
  align-items: center;
  background: #f9fafb;
  border: 1px solid #d7dde7;
  border-radius: 8px;
  padding: 4px 4px 4px 20px;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.input-wrap:focus-within {
  border-color: #2563eb;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, .12);
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
  color: #111827;
  font-size: 15px;
  padding: 10px 0;
}

.lock-input::placeholder {
  color: #a6afbd;
}

.unlock-btn {
  width: 42px;
  height: 42px;
  border-radius: 7px;
  background: #2563eb;
  border: none;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  transition: transform 0.2s, opacity 0.2s, background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.unlock-btn:hover:not(:disabled) {
  transform: scale(1.08);
  background: #1d4ed8;
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
  color: #4b5563;
  font-size: 13px;
  text-decoration: none;
  transition: color 0.2s;
}

.lock-footer a:hover {
  color: #2563eb;
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
