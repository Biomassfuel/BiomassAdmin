<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo-mark" alt="logo">
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo-mark" alt="logo">
        <h1 class="sidebar-title">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import variables from '@/assets/styles/variables.scss'
import logo from '@/assets/logo/logo.png'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    },
    navType() {
      return this.$store.state.settings.navType
    }
  },
  data() {
    return {
      logo,
      title: process.env.VUE_APP_TITLE
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity .24s ease;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 60px;
  background: #ffffff;
  text-align: left;
  overflow: hidden;
  border-bottom: 1px solid #edf1f7;

  & .sidebar-logo-link {
    display: flex !important;
    align-items: center;
    gap: 11px;
    height: 100%;
    width: 100%;
    padding: 0 18px 0 20px;
    line-height: 1;

    & .sidebar-logo-mark {
      flex: 0 0 auto;
      width: 38px;
      height: 38px;
      border-radius: 10px;
      object-fit: contain;
    }

    & .sidebar-title {
      display: block;
      margin: 0;
      max-width: 152px;
      overflow: hidden;
      color: #1f2329;
      font-weight: 700;
      line-height: 20px;
      font-size: 15px;
      font-family: Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", PingFang SC, Microsoft YaHei, sans-serif;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &.collapse {
    .sidebar-logo-link {
      justify-content: center;
      padding: 0;
    }
  }
}
</style>
