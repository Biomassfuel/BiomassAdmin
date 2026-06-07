<template>
  <div class="navbar" :class="'nav' + navType">
    <div class="nav-left">
      <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
      <breadcrumb v-if="navType == 1" id="breadcrumb-container" class="breadcrumb-container" />
      <top-nav v-if="navType == 2" id="topmenu-container" class="topmenu-container" />
      <template v-if="navType == 3">
        <logo v-show="showLogo" :collapse="false"></logo>
        <top-bar id="topbar-container" class="topbar-container" />
      </template>
    </div>
    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <header-notice id="header-notice" class="right-menu-item hover-effect" />

      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <span class="user-nickname"> {{ nickName }} </span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setLayout" v-if="setting">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item @click.native="lockScreen">
            <span>锁定屏幕</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from './TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import Search from '@/components/HeaderSearch'
import HeaderNotice from './HeaderNotice'

export default {
  components: {
    Breadcrumb,
    Logo,
    TopNav,
    TopBar,
    Hamburger,
    Screenfull,
    Search,
    HeaderNotice
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'nickName'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    navType: {
      get() {
        return this.$store.state.settings.navType
      }
    },
    showLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    setLayout(event) {
      this.$emit('setLayout')
    },
    lockScreen() {
      const currentPath = this.$route.fullPath
      this.$store.dispatch('lock/lockScreen', currentPath).then(() => {
        this.$router.push('/lock')
      })
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar.nav3 {
  .hamburger-container {
    display: none !important;
  }
}

.navbar {
  height: 58px;
  overflow: hidden;
  position: relative;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  padding: 0 20px 0 14px;
  border-bottom: 1px solid #e5e7eb;
  background: rgba(255, 255, 255, 0.94);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 0 rgba(15, 23, 42, 0.025);

  .nav-left {
    display: flex;
    align-items: center;
    min-width: 0;
    height: 100%;
  }

  .hamburger-container {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: 34px;
    height: 34px;
    margin-right: 10px;
    border-radius: 8px;
    color: #4b5563;
    line-height: 34px;
    cursor: pointer;
    transition: background .18s ease, color .18s ease, box-shadow .18s ease;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: #f3f4f6;
      color: #111827;
      box-shadow: inset 0 0 0 1px #e5e7eb;
    }
  }

  .breadcrumb-container {
    flex-shrink: 0;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .topbar-container {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    overflow: hidden;
    margin-left: 8px;
  }

  .right-menu {
    height: 100%;
    line-height: 58px;
    display: flex;
    align-items: center;
    margin-left: auto;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 34px;
      height: 34px;
      padding: 0 8px;
      border-radius: 8px;
      font-size: 17px;
      color: #4b5563;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .18s ease, color .18s ease, box-shadow .18s ease;

        &:hover {
          background: #f3f4f6;
          color: #111827;
          box-shadow: inset 0 0 0 1px #e5e7eb;
        }
      }
    }

    .avatar-container {
      margin-right: 0px;
      padding-right: 0px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-top: 0;
        right: 0;
        position: relative;
        height: 34px;
        padding: 0 8px 0 4px;
        border-radius: 999px;
        transition: background .18s ease, box-shadow .18s ease;

        &:hover {
          background: #f3f4f6;
          box-shadow: inset 0 0 0 1px #e5e7eb;
        }

        .user-avatar {
          cursor: pointer;
          width: 28px;
          height: 28px;
          border: 1px solid #e5e7eb;
          border-radius: 50%;
        }

        .user-nickname{
          position: static;
          max-width: 120px;
          overflow: hidden;
          color: #111827;
          font-size: 13px;
          font-weight: 600;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
