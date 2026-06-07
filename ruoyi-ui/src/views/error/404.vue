<template>
  <div class="error-page">
    <section class="error-card">
      <div class="error-visual">
        <span class="error-code">404</span>
        <span class="error-ring"></span>
        <span class="error-ring error-ring--small"></span>
      </div>
      <div class="error-copy">
        <p class="error-label">页面不存在</p>
        <h1>{{ message }}</h1>
        <p class="error-desc">当前页面可能已移动、删除，或地址输入有误。可以回到首页，从左侧菜单重新进入需要的功能。</p>
        <router-link to="/">
          <el-button type="primary" icon="el-icon-s-home">返回首页</el-button>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script>

export default {
  name: 'Page404',
  computed: {
    message() {
      return '找不到网页！'
    }
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 32px;
  background: #f3f4f6;
}

.error-card {
  display: grid;
  grid-template-columns: 300px minmax(0, 430px);
  gap: 34px;
  align-items: center;
  width: min(860px, 100%);
  padding: 40px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: rgba(255, 255, 255, .94);
  box-shadow: 0 12px 34px rgba(15, 23, 42, 0.08);
}

.error-visual {
  position: relative;
  display: grid;
  place-items: center;
  min-height: 260px;
  overflow: hidden;
  border-radius: 10px;
  background: #f9fafb;
}

.error-code {
  position: relative;
  z-index: 2;
  color: #2563eb;
  font-size: 72px;
  font-weight: 800;
  letter-spacing: 0;
}

.error-ring {
  position: absolute;
  width: 170px;
  height: 170px;
  border: 26px solid rgba(37, 99, 235, .12);
  border-radius: 50%;
}

.error-ring--small {
  right: 34px;
  bottom: 26px;
  width: 78px;
  height: 78px;
  border-width: 14px;
  border-color: rgba(5, 150, 105, .14);
}

.error-label {
  margin: 0 0 10px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
  line-height: 20px;
}

h1 {
  margin: 0;
  color: #111827;
  font-size: 30px;
  font-weight: 750;
  line-height: 40px;
}

.error-desc {
  margin: 14px 0 26px;
  color: #4b5563;
  font-size: 14px;
  line-height: 24px;
}

@media (max-width: 760px) {
  .error-card {
    grid-template-columns: 1fr;
    padding: 26px;
  }

  .error-visual {
    min-height: 180px;
  }

  .error-code {
    font-size: 54px;
  }
}
</style>
