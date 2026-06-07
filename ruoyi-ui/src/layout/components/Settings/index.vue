<template>
  <el-drawer size="320px" :visible="showSettings" :with-header="false" :append-to-body="true" :before-close="closeSetting" :lock-scroll="false">
    <div class="drawer-container">
      <div>
        <div class="setting-drawer-content">
          <div class="setting-drawer-title">
            <h3 class="drawer-title">菜单导航设置</h3>
          </div>
          <div class="nav-wrap">
            <el-tooltip content="左侧菜单" placement="bottom">
              <div class="item left" @click="handleNavType(1)" :style="{'--theme': theme}" :class="{ activeItem: navType == 1 }">
                <b></b><b></b>
              </div>
            </el-tooltip>

            <el-tooltip content="混合菜单" placement="bottom">
              <div class="item mix" @click="handleNavType(2)" :style="{'--theme': theme}" :class="{ activeItem: navType == 2 }">
                <b></b><b></b>
              </div>
            </el-tooltip>
            <el-tooltip content="顶部菜单" placement="bottom">
              <div class="item top" @click="handleNavType(3)" :style="{'--theme': theme}" :class="{ activeItem: navType == 3 }">
                <b></b><b></b>
              </div>
            </el-tooltip>
          </div>
          <div class="setting-drawer-title">
            <h3 class="drawer-title">主题风格设置</h3>
          </div>
          <div class="setting-drawer-block-checbox">
            <div class="setting-drawer-block-checbox-item light-preview is-active" @click="handleTheme('theme-light')">
              <img src="@/assets/images/light.svg" alt="light">
              <span>浅色工作台</span>
              <div class="setting-drawer-block-checbox-selectIcon" style="display: block;">
                <i aria-label="图标: check" class="anticon anticon-check">
                  <svg viewBox="64 64 896 896" data-icon="check" width="1em" height="1em" :fill="theme" aria-hidden="true" focusable="false" class="">
                    <path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"/>
                  </svg>
                </i>
              </div>
            </div>
          </div>

          <div class="drawer-item">
            <span>主题颜色</span>
            <theme-picker style="float: right;height: 26px;margin: -3px 8px 0 0;" @change="themeChange" />
          </div>
        </div>

        <el-divider/>

        <h3 class="drawer-title">系统布局配置</h3>

        <div class="drawer-item">
          <span>开启页签</span>
          <el-switch v-model="tagsView" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>持久化标签页</span>
          <el-switch v-model="tagsViewPersist" :disabled="!tagsView" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>显示页签图标</span>
          <el-switch v-model="tagsIcon" :disabled="!tagsView" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>标签页样式</span>
          <el-radio-group v-model="tagsViewStyle" :disabled="!tagsView" size="mini" class="drawer-switch">
            <el-radio-button label="card">卡片</el-radio-button>
            <el-radio-button label="chrome">谷歌</el-radio-button>
          </el-radio-group>
        </div>

        <div class="drawer-item">
          <span>固定 Header</span>
          <el-switch v-model="fixedHeader" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>显示 Logo</span>
          <el-switch v-model="sidebarLogo" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>动态标题</span>
          <el-switch v-model="dynamicTitle" class="drawer-switch" />
        </div>

        <div class="drawer-item">
          <span>底部版权</span>
          <el-switch v-model="footerVisible" class="drawer-switch" />
        </div>

        <el-divider/>

        <el-button size="small" type="primary" plain icon="el-icon-document-add" @click="saveSetting">保存配置</el-button>
        <el-button size="small" plain icon="el-icon-refresh" @click="resetSetting">重置配置</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import ThemePicker from '@/components/ThemePicker'

export default {
  components: { ThemePicker },
  expose: ['openSetting'],
  data() {
    return {
      theme: this.$store.state.settings.theme,
      sideTheme: this.$store.state.settings.sideTheme,
      navType: this.$store.state.settings.navType,
      showSettings: false
    }
  },
  computed: {
    fixedHeader: {
      get() {
        return this.$store.state.settings.fixedHeader
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'fixedHeader',
          value: val
        })
      }
    },
    tagsViewPersist: {
      get() {
        return this.$store.state.settings.tagsViewPersist
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsViewPersist',
          value: val
        })
      }
    },
    tagsView: {
      get() {
        return this.$store.state.settings.tagsView
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsView',
          value: val
        })
      }
    },
    tagsIcon: {
      get() {
        return this.$store.state.settings.tagsIcon
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsIcon',
          value: val
        })
      }
    },
    tagsViewStyle: {
      get() {
        return this.$store.state.settings.tagsViewStyle
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'tagsViewStyle',
          value: val
        })
      }
    },
    sidebarLogo: {
      get() {
        return this.$store.state.settings.sidebarLogo
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'sidebarLogo',
          value: val
        })
      }
    },
    dynamicTitle: {
      get() {
        return this.$store.state.settings.dynamicTitle
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'dynamicTitle',
          value: val
        })
        this.$store.dispatch('settings/setTitle', this.$store.state.settings.title)
      }
    },
    footerVisible: {
      get() {
        return this.$store.state.settings.footerVisible
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'footerVisible',
          value: val
        })
      }
    }
  },
  watch: {
    navType: {
      handler(val) {
        if (val == 1) {
          this.$store.dispatch("app/toggleSideBarHide", false)
        }
        if (val == 2) {
        }
        if (val == 3) {
          this.$store.dispatch("app/toggleSideBarHide", true)
        }
        if ([1, 3].includes(val)) {
          this.$store.commit("SET_SIDEBAR_ROUTERS",this.$store.state.permission.defaultRoutes)
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    themeChange(val) {
      this.$store.dispatch('settings/changeSetting', {
        key: 'theme',
        value: val
      })
      this.theme = val
    },
    handleTheme(val) {
      val = 'theme-light'
      this.$store.dispatch('settings/changeSetting', {
        key: 'sideTheme',
        value: val
      })
      this.sideTheme = val
    },
    handleNavType(val) {
      this.$store.dispatch('settings/changeSetting', {
        key: 'navType',
        value: val
      })
      this.navType = val
    },
    openSetting() {
      this.showSettings = true
    },
    closeSetting(){
      this.showSettings = false
    },
    saveSetting() {
      this.$modal.loading("正在保存到本地，请稍候...")
      if (!this.tagsViewPersist) {
        this.$cache.local.remove('tags-view-visited')
      }
      this.$cache.local.set(
        "layout-setting",
        `{
            "navType":${this.navType},
            "tagsView":${this.tagsView},
            "tagsIcon":${this.tagsIcon},
            "tagsViewStyle":"${this.tagsViewStyle}",
            "tagsViewPersist":${this.tagsViewPersist},
            "fixedHeader":${this.fixedHeader},
            "sidebarLogo":${this.sidebarLogo},
            "dynamicTitle":${this.dynamicTitle},
            "footerVisible":${this.footerVisible},
            "sideTheme":"theme-light",
            "theme":"${this.theme}"
          }`
      )
      setTimeout(this.$modal.closeLoading(), 1000)
    },
    resetSetting() {
      this.$modal.loading("正在清除设置缓存并刷新，请稍候...")
      this.$cache.local.remove('tags-view-visited')
      this.$cache.local.remove("layout-setting")
      setTimeout("window.location.reload()", 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.setting-drawer-content {
  .setting-drawer-title {
    margin-bottom: 10px;
    color: #1d2129;
    font-size: 14px;
    line-height: 22px;
    font-weight: 650;
  }

  .setting-drawer-block-checbox {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 12px;
    margin: 10px 0 20px;

    .setting-drawer-block-checbox-item {
      position: relative;
      width: 142px;
      height: 72px;
      overflow: hidden;
      border: 1px solid #edf1f7;
      border-radius: 8px;
      background: #f8fafc;
      cursor: pointer;
      transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;

      &:hover {
        border-color: #b8c7e6;
        box-shadow: 0 8px 20px rgba(29, 33, 41, 0.08);
        transform: translateY(-1px);
      }

      img {
        width: 100%;
        height: 48px;
        object-fit: cover;
      }

      span {
        display: block;
        padding: 2px 10px 0;
        color: #4e5969;
        font-size: 12px;
        font-weight: 600;
        line-height: 18px;
      }

      &.is-active {
        border-color: #b8c7e6;
        background: #ffffff;
      }

      .setting-drawer-block-checbox-selectIcon {
        position: absolute;
        right: 6px;
        bottom: 5px;
        width: 18px;
        height: 18px;
        padding: 0;
        border-radius: 50%;
        background: #ffffff;
        color: #2468f2;
        font-weight: 700;
        font-size: 12px;
        line-height: 18px;
        text-align: center;
        box-shadow: 0 4px 10px rgba(29, 33, 41, 0.12);
      }
    }
  }
}

.drawer-container {
  min-height: 100%;
  padding: 22px;
  background: #ffffff;
  color: #1d2129;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;

  .drawer-title {
    margin: 0 0 12px;
    color: #1d2129;
    font-size: 14px;
    line-height: 22px;
    font-weight: 650;
  }

  .drawer-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 42px;
    padding: 9px 0;
    color: #4e5969;
    font-size: 13px;

    span {
      color: #4e5969;
      font-weight: 500;
    }
  }

  .drawer-switch {
    float: none;
    flex: 0 0 auto;
  }
}

::v-deep .el-divider {
  margin: 18px 0;
  background-color: #edf1f7;
}

::v-deep .el-radio-button__inner {
  padding: 7px 10px;
  border-color: #e5e8ef;
  font-size: 12px;
}

::v-deep .el-switch.is-checked .el-switch__core {
  border-color: #2468f2;
  background-color: #2468f2;
}

::v-deep .el-button {
  margin: 0 8px 8px 0;
}

// 导航模式
.nav-wrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 12px;
  margin: 10px 0 20px;

  .activeItem {
    border-color: #{'var(--theme)'} !important;
    box-shadow: 0 8px 18px rgba(36, 104, 242, 0.12);
  }

  .item {
    position: relative;
    width: 56px;
    height: 48px;
    margin-right: 0;
    border: 1px solid #edf1f7;
    border-radius: 8px;
    background: #f8fafc;
    cursor: pointer;
    transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;

    &:hover {
      border-color: #b8c7e6;
      box-shadow: 0 8px 20px rgba(29, 33, 41, 0.08);
      transform: translateY(-1px);
    }
  }

  .left {
    b:first-child {
      display: block;
      height: 30%;
      background: #fff;
      border-bottom: 1px solid #edf1f7;
    }
    b:last-child {
      width: 30%;
      background: #2468f2;
      position: absolute;
      height: 100%;
      top: 0;
      border-radius: 8px 0 0 8px;
    }
  }
  .mix {
    b:first-child {
      border-radius: 8px 8px 0 0;
      display: block;
      height: 30%;
      background: #2468f2;
    }
    b:last-child {
      width: 30%;
      background: #2468f2;
      position: absolute;
      height: 70%;
      border-radius: 0 0 0 8px;
    }
  }
  .top {
    b:first-child {
      display: block;
      height: 30%;
      background: #2468f2;
      border-radius: 8px 8px 0 0;
    }
  }
}
</style>
