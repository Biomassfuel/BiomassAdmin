<template>
  <el-menu class="topbar-menu" :default-active="activeMenu" :active-text-color="theme" mode="horizontal">
    <sidebar-item :key="route.path + index" v-for="(route, index) in topMenus" :item="route" :base-path="route.path" />

    <el-submenu index="more" class="el-submenu__hide-arrow" v-if="moreRoutes.length > 0">
      <template slot="title">更多菜单</template>
      <sidebar-item :key="route.path + index" v-for="(route, index) in moreRoutes" :item="route" :base-path="route.path" />
    </el-submenu>
  </el-menu>
</template>

<script>
import SidebarItem from '../Sidebar/SidebarItem'

export default {
  components: { SidebarItem },
  data() {
    return {
      // 顶部栏初始数
      visibleNumber: 5
    }
  },
  computed: {
    theme() {
      return this.$store.state.settings.theme
    },
    topMenus() {
      return this.$store.state.permission.sidebarRouters.filter((f) => !f.hidden).slice(0, this.visibleNumber)
    },
    moreRoutes() {
      return this.$store.state.permission.sidebarRouters.filter((f) => !f.hidden).slice(this.visibleNumber)
    },
    // 默认激活的菜单
    activeMenu() {
      const { meta, path } = this.$route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
  },
  beforeMount() {
    window.addEventListener('resize', this.setVisibleNumber)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.setVisibleNumber)
  },
  mounted() {
    this.setVisibleNumber()
  },
  methods: {
    // 根据宽度计算设置显示栏数
    setVisibleNumber() {
      const width = document.body.getBoundingClientRect().width / 3
      this.visibleNumber = Math.max(1, parseInt(width / 92))
    }
  }
}
</script>

<style lang="scss">
.topbar-menu.el-menu--horizontal {
  display: flex;
  align-items: center;
  height: 58px;
  min-width: 0;
  border-bottom: none;
  background: transparent;
}

.topbar-menu.el-menu--horizontal .el-submenu__title,
.topbar-menu.el-menu--horizontal .el-menu-item {
  height: 34px !important;
  margin: 0 4px !important;
  padding: 0 12px !important;
  border-bottom: none !important;
  border-radius: 8px;
  color: #4e5969 !important;
  font-size: 13px;
  font-weight: 500;
  line-height: 34px !important;
  transition: background 0.16s ease, color 0.16s ease;

  .svg-icon {
    margin-right: 6px;
  }

  &:hover,
  &:focus {
    background: #f4f7fb !important;
    color: #1d2129 !important;
    box-shadow: inset 0 0 0 1px #edf1f7;
  }
}

.topbar-menu.el-menu--horizontal > .el-menu-item.is-active,
.topbar-menu.el-menu--horizontal > .el-submenu.is-active .el-submenu__title {
  background: #edf4ff !important;
  color: var(--current-color, #2468f2) !important;
  font-weight: 600;
  box-shadow: inset 0 0 0 1px #d7e6ff;
}

.topbar-menu .el-submenu .el-submenu__icon-arrow {
  position: static;
  vertical-align: middle;
  margin-top: 0;
  margin-left: 6px;
  color: #86909c;
}

.el-menu--horizontal .el-menu--popup {
  padding: 6px;
  border: 1px solid #edf1f7;
  border-radius: 10px;
  box-shadow: 0 12px 32px rgba(29, 33, 41, 0.12);
}

.el-menu--horizontal .el-menu--popup .el-menu-item,
.el-menu--horizontal .el-menu--popup .el-submenu__title {
  height: 38px !important;
  border-radius: 8px;
  color: #4e5969 !important;
  line-height: 38px !important;
}

.el-menu--horizontal .el-menu--popup .el-menu-item:hover,
.el-menu--horizontal .el-menu--popup .el-submenu__title:hover {
  background-color: #f4f7fb !important;
  color: #1d2129 !important;
}
</style>
