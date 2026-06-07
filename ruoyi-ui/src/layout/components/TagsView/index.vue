<template>
  <div id="tags-view-container" class="tags-view-container" :class="{ 'tags-view-container--chrome': tagsViewStyle === 'chrome' }" :style="chromeVars">
    <!-- 左切换箭头 -->
    <span class="tags-nav-btn tags-nav-btn--left" :class="{ disabled: !canScrollLeft }" @click="scrollLeft">
      <i class="el-icon-arrow-left" />
    </span>

    <!-- 标签滚动区 -->
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll" @updateArrows="updateArrowState">
      <router-link
        v-for="tag in visitedViews"
        ref="tag"
        :key="tag.path"
        :class="{ 'active': isActive(tag), 'has-icon': tagsIcon }"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="span"
        class="tags-view-item"
        :style="tagActiveStyle(tag)"
        @click.middle.native="!isAffix(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent.native="openMenu(tag, $event)"
      >
        <svg-icon v-if="tagsIcon && tag.meta && tag.meta.icon && tag.meta.icon !== '#'" :icon-class="tag.meta.icon" style="margin-right: 3px;" />
        {{ tag.title }}
        <span v-if="!isAffix(tag)" class="el-icon-close" @click.prevent.stop="closeSelectedTag(tag)" />
      </router-link>
    </scroll-pane>

    <!-- 右切换箭头 -->
    <span class="tags-nav-btn tags-nav-btn--right" :class="{ disabled: !canScrollRight }" @click="scrollRight">
      <i class="el-icon-arrow-right" />
    </span>

    <!-- 下拉操作菜单 -->
    <el-dropdown class="tags-action-dropdown" trigger="click" placement="bottom-end" @command="handleDropdownCommand">
      <span class="tags-action-btn">
        <i class="el-icon-arrow-down" />
      </span>
      <el-dropdown-menu slot="dropdown" class="tags-dropdown-menu">
        <el-dropdown-item v-if="!isAffix(selectedDropdownTag)" command="close" icon="el-icon-close">关闭当前</el-dropdown-item>
        <el-dropdown-item command="closeOthers" icon="el-icon-circle-close">关闭其他</el-dropdown-item>
        <el-dropdown-item command="closeLeft" :disabled="isFirstView()" icon="el-icon-back">关闭左侧</el-dropdown-item>
        <el-dropdown-item command="closeRight" :disabled="isLastView()" icon="el-icon-right">关闭右侧</el-dropdown-item>
        <el-dropdown-item command="closeAll" icon="el-icon-circle-close">全部关闭</el-dropdown-item>
        <el-dropdown-item command="fullscreen" divided>
          <template v-if="!isFullscreen"><i class="el-icon-full-screen"></i>全屏显示</template>
          <template v-else><i class="el-icon-close"></i>退出全屏</template>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

    <!-- 刷新按钮 -->
    <span class="tags-action-btn tags-refresh-btn" title="刷新页面" @click="refreshSelectedTag(selectedDropdownTag)">
      <i class="el-icon-refresh-right" /> 刷新
    </span>

    <!-- 右键上下文菜单 -->
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)"><i class="el-icon-refresh-right"></i> 刷新页面</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)"><i class="el-icon-close"></i> 关闭当前</li>
      <li @click="closeOthersTags"><i class="el-icon-circle-close"></i> 关闭其他</li>
      <li v-if="!isFirstView()" @click="closeLeftTags"><i class="el-icon-back"></i> 关闭左侧</li>
      <li v-if="!isLastView()" @click="closeRightTags"><i class="el-icon-right"></i> 关闭右侧</li>
      <li @click="closeAllTags(selectedTag)"><i class="el-icon-circle-close"></i> 全部关闭</li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'
import path from 'path'

export default {
  components: { ScrollPane },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: [],
      canScrollLeft: false,
      canScrollRight: false,
      isFullscreen: false,
      hiddenElements: []
    }
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews
    },
    routes() {
      return this.$store.state.permission.routes
    },
    theme() {
      return this.$store.state.settings.theme
    },
    tagsIcon() {
      return this.$store.state.settings.tagsIcon
    },
    tagsViewStyle() {
      return this.$store.state.settings.tagsViewStyle
    },
    selectedDropdownTag() {
      return this.visitedViews.find(v => this.isActive(v)) || {}
    },
    chromeVars() {
      if (this.tagsViewStyle !== 'chrome') return {}
      const primary = this.theme || '#2468f2'
      return {
        '--chrome-tab-active-bg': this.mixHexWithWhite(primary, 0.15),
        '--chrome-tab-text-active': primary,
        '--chrome-wing-r': '14px'
      }
    }
  },
  watch: {
    $route() {
      this.addTags()
      this.moveToCurrentTag()
    },
    visible(value) {
      if (value) {
        document.body.addEventListener('click', this.closeMenu)
      } else {
        document.body.removeEventListener('click', this.closeMenu)
      }
    },
    visitedViews() {
      this.$nextTick(() => {
        this.updateArrowState()
      })
    }
  },
  mounted() {
    this.initTags()
    this.addTags()
    window.addEventListener('resize', this.updateArrowState)
    window.addEventListener('keydown', this.handleKeyDown)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateArrowState)
    window.removeEventListener('keydown', this.handleKeyDown)
  },
  methods: {
    handleKeyDown(event) {
      // 当按下Esc键且处于全屏状态时，退出全屏
      if (event.key === 'Escape' && this.isFullscreen) {
        this.toggleFullscreen()
      }
    },
    mixHexWithWhite(hex, ratio) {
      const clean = hex.replace('#', '')
      const r = parseInt(clean.substring(0, 2), 16)
      const g = parseInt(clean.substring(2, 4), 16)
      const b = parseInt(clean.substring(4, 6), 16)
      const mr = Math.round(r * ratio + 255 * (1 - ratio))
      const mg = Math.round(g * ratio + 255 * (1 - ratio))
      const mb = Math.round(b * ratio + 255 * (1 - ratio))
      return `rgb(${mr}, ${mg}, ${mb})`
    },
    isActive(route) {
      return route.path === this.$route.path
    },
    tagActiveStyle(tag) {
      return {}
    },
    isAffix(tag) {
      return tag && tag.meta && tag.meta.affix
    },
    isFirstView() {
      try {
        const tag = this.selectedTag && this.selectedTag.fullPath ? this.selectedTag : this.selectedDropdownTag
        return tag.fullPath === '/index' || tag.fullPath === this.visitedViews[1].fullPath
      } catch (err) {
        return false
      }
    },
    isLastView() {
      try {
        const tag = this.selectedTag && this.selectedTag.fullPath ? this.selectedTag : this.selectedDropdownTag
        return tag.fullPath === this.visitedViews[this.visitedViews.length - 1].fullPath
      } catch (err) {
        return false
      }
    },
    filterAffixTags(routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    },
    initTags() {
      if (this.$store.state.settings.tagsViewPersist) {
        this.$store.dispatch('tagsView/loadPersistedViews')
      }
      const affixTags = this.affixTags = this.filterAffixTags(this.routes)
      for (const tag of affixTags) {
        if (tag.name) {
          this.$store.dispatch('tagsView/addAffixView', tag)
        }
      }
    },
    addTags() {
      const { name } = this.$route
      if (name) {
        this.$store.dispatch('tagsView/addView', this.$route)
      }
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag)
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch('tagsView/updateVisitedView', this.$route)
            }
            break
          }
        }
      })
    },
    scrollLeft() {
      if (!this.canScrollLeft) return
      this.$refs.scrollPane.scrollToStart()
    },
    scrollRight() {
      if (!this.canScrollRight) return
      this.$refs.scrollPane.scrollToEnd()
    },
    updateArrowState() {
      this.$nextTick(() => {
        if (this.$refs.scrollPane) {
          const state = this.$refs.scrollPane.getScrollState()
          this.canScrollLeft = state.canLeft
          this.canScrollRight = state.canRight
        }
      })
    },
    toggleFullscreen() {
      const mainContainer = document.querySelector('.main-container')
      const navbar = document.querySelector('.navbar')
      const sidebar = document.querySelector('.sidebar-container')
      if (!mainContainer) return

      if (!this.isFullscreen) {
        mainContainer.classList.add('fullscreen-mode')
        document.body.style.overflow = 'hidden'
        const elementsToHide = [
          { el: navbar, originalDisplay: (navbar && navbar.style.display) || '' }, 
          { el: sidebar, originalDisplay: (sidebar && sidebar.style.display) || '' }
        ]
        elementsToHide.forEach(item => {
          if (item.el && item.el.style.display !== 'none') {
            item.originalDisplay = item.el.style.display
            item.el.style.display = 'none'
            this.hiddenElements.push(item)
          }
        })
        this.isFullscreen = true
      } else {
        mainContainer.classList.remove('fullscreen-mode')
        document.body.style.overflow = ''
        this.hiddenElements.forEach(item => {
          if (item.el) {
            item.el.style.display = item.originalDisplay
          }
        })
        this.hiddenElements = []
        const btn = document.querySelector('.tags-action-btn')
        if (btn) btn.blur()
        this.isFullscreen = false
      }
    },
    handleDropdownCommand(command) {
      const tag = this.selectedDropdownTag
      this.selectedTag = tag
      switch (command) {
        case 'refresh':
          this.refreshSelectedTag(tag)
          break
        case 'fullscreen':
          this.toggleFullscreen()
          break
        case 'close':
          this.closeSelectedTag(tag)
          break
        case 'closeOthers':
          this.closeOthersTags()
          break
        case 'closeLeft':
          this.closeLeftTags()
          break
        case 'closeRight':
          this.closeRightTags()
          break
        case 'closeAll':
          this.closeAllTags(tag)
          break
      }
    },
    refreshSelectedTag(view) {
      this.$tab.refreshPage(view)
      if (this.$route.meta.link) {
        this.$store.dispatch('tagsView/delIframeView', this.$route)
      }
    },
    closeSelectedTag(view) {
      this.$tab.closePage(view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view)
        }
      })
    },
    closeRightTags() {
      this.$tab.closeRightPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeLeftTags() {
      this.$tab.closeLeftPage(this.selectedTag).then(visitedViews => {
        if (!visitedViews.find(i => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews)
        }
      })
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag.fullPath).catch(() => {})
      this.$tab.closeOtherPage(this.selectedTag).then(() => {
        this.moveToCurrentTag()
      })
    },
    closeAllTags(view) {
      this.$tab.closeAllPage().then(({ visitedViews }) => {
        if (this.affixTags.some(tag => tag.path === this.$route.path)) {
          return
        }
        this.toLastView(visitedViews, view)
      })
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        if (view && view.name === 'Dashboard') {
          this.$router.replace({ path: '/redirect' + view.fullPath })
        } else {
          this.$router.push('/')
        }
      }
    },
    openMenu(tag, e) {
      this.left = e.clientX
      this.top = e.clientY
      this.visible = true
      this.selectedTag = tag
    },
    closeMenu() {
      this.visible = false
    },
    handleScroll() {
      this.closeMenu()
      this.updateArrowState()
    }
  }
}
</script>

<style lang="scss" scoped>
$tags-bar-height: 38px;

.tags-view-container {
  display: flex;
  align-items: center;
  width: 100%;
  height: $tags-bar-height;
  overflow: hidden;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;

  $btn-width: 34px;
  $btn-color: #6b7280;
  $btn-hover-bg: #f3f4f6;
  $btn-hover-color: #111827;
  $btn-disabled-color: #d1d5db;
  $divider: 1px solid #e5e7eb;

  .tags-nav-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: $btn-width;
    height: $tags-bar-height;
    color: $btn-color;
    font-size: 13px;
    cursor: pointer;
    user-select: none;
    transition: background 0.15s, color 0.15s;

    &:hover:not(.disabled) {
      background: $btn-hover-bg;
      color: $btn-hover-color;
    }

    &.disabled {
      color: $btn-disabled-color;
      cursor: not-allowed;
    }

    &--left { border-right: $divider; }
    &--right { border-left: $divider; }
  }

  .tags-view-wrapper {
    flex: 1;
    min-width: 0;
    height: 100%;

    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      height: 28px;
      margin-left: 6px;
      padding: 0 10px;
      border: 1px solid transparent;
      border-radius: 7px;
      background: transparent;
      color: #4b5563;
      font-size: 12px;
      font-weight: 500;
      line-height: 28px;
      cursor: pointer;
      transition: color 0.16s, background 0.16s, border-color 0.16s;

      &:first-of-type { margin-left: 10px; }
      &:last-of-type { margin-right: 12px; }

      &:hover {
        background: #f3f4f6;
        color: #111827;
      }
    }
  }

  &:not(.tags-view-container--chrome) .tags-view-wrapper .tags-view-item.active {
    color: #2563eb;
    border-color: #bfdbfe;
    background: #eff6ff;
    font-weight: 600;

    &::before {
      content: '';
      display: none;
    }
  }

  .tags-action-dropdown {
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }

  .tags-action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: $btn-width;
    height: $tags-bar-height;
    border-left: $divider;
    color: $btn-color;
    font-size: 13px;
    cursor: pointer;
    user-select: none;
    transition: background 0.15s, color 0.15s;

    &:hover {
      background: $btn-hover-bg;
      color: $btn-hover-color;
    }
  }

  .tags-refresh-btn {
    width: 64px;
    gap: 3px;
  }

  .contextmenu {
    position: fixed;
    z-index: 3000;
    margin: 0;
    padding: 6px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    background: #fff;
    color: #111827;
    list-style-type: none;
    box-shadow: 0 12px 32px rgba(29, 33, 41, 0.12);
    font-size: 12px;
    font-weight: 400;

    li {
      margin: 0;
      padding: 8px 12px;
      border-radius: 6px;
      cursor: pointer;

      &:hover {
        background: #f3f4f6;
      }
    }
  }
  &.tags-view-container--chrome {
    --chrome-strip-bg: #ffffff;
    --chrome-strip-border: #e4e7ed;
    --chrome-tab-text: #606266;

    overflow: visible;
    background: var(--chrome-strip-bg);
    border-bottom: 1px solid var(--chrome-strip-border);
    align-items: flex-end;

    .tags-nav-btn {
      align-self: stretch;
      height: auto;
      min-height: $tags-bar-height;
      border-color: var(--chrome-strip-border);
    }

    .tags-action-btn {
      border-color: var(--chrome-strip-border);
    }

    .tags-view-wrapper {
      .tags-view-item {
        display: inline-flex !important;
        align-items: center;
        justify-content: center;
        position: relative;
        z-index: 1;
        height: 30px;
        min-height: 30px;
        margin: 0 0 -1px;
        padding: 0 12px;
        font-size: 13px;
        font-weight: 400;
        line-height: 1.2;
        border: none !important;
        border-radius: 0;
        background: transparent !important;
        color: var(--chrome-tab-text) !important;
        padding-top: 0 !important;
        box-shadow: none !important;
        transition: background 0.12s ease, color 0.12s ease, border-radius 0.12s ease;

        &::before,
        &::after {
          content: '' !important;
          display: block !important;
          position: absolute;
          bottom: 0;
          width: var(--chrome-wing-r);
          height: var(--chrome-wing-r);
          margin: 0 !important;
          pointer-events: none;
          background: transparent !important;
          border-radius: 0 !important;
          transition: box-shadow 0.12s ease;
        }

        &::before {
          left: calc(-1 * var(--chrome-wing-r));
          border-bottom-right-radius: var(--chrome-wing-r) !important;
          box-shadow: none;
        }

        &::after {
          right: calc(-1 * var(--chrome-wing-r));
          border-bottom-left-radius: var(--chrome-wing-r) !important;
          box-shadow: none;
        }

        &:first-of-type { margin-left: 6px; }
        &:last-of-type  { margin-right: 10px; }

        &:not(.active) + .tags-view-item:not(.active) {
          border-left: 1px solid #e4e7ed;
          padding-left: 11px;
        }

        &:hover:not(.active) {
          background: #f5f7fa !important;
          border-radius: 6px 6px 0 0;
          color: #303133 !important;
        }

        &.active {
          height: 31px;
          min-height: 31px;
          padding: 0 14px;
          color: var(--chrome-tab-text-active) !important;
          font-weight: 500;
          background: var(--chrome-tab-active-bg) !important;
          border: none !important;
          border-radius: var(--chrome-wing-r) var(--chrome-wing-r) 0 0;
          box-shadow: 0 1px 4px rgba(29, 33, 41, 0.06);

          &::before {
            box-shadow: calc(var(--chrome-wing-r) * 0.5) calc(var(--chrome-wing-r) * 0.5) 0 calc(var(--chrome-wing-r) * 0.5) var(--chrome-tab-active-bg);
          }

          &::after {
            box-shadow: calc(var(--chrome-wing-r) * -0.5) calc(var(--chrome-wing-r) * 0.5) 0 calc(var(--chrome-wing-r) * 0.5) var(--chrome-tab-active-bg);
          }
        }
        .el-icon-close {
          margin-left: 3px;
          &:before {
            vertical-align: -2px;
          }
        }
      }
    }
  }
}
</style>

<style lang="scss">
.tags-view-wrapper {
  .el-scrollbar {
    height: 100%;
    overflow: hidden;
  }

  .el-scrollbar__wrap {
    height: 38px !important;
    display: flex;
    align-items: center;
    overflow-x: auto;
    overflow-y: hidden;
    
    &::-webkit-scrollbar {
      width: 0;
      height: 0;
    }
    
    .tags-view-container:hover & {
      &::-webkit-scrollbar {
        width: 6px;
        height: 6px;
      }
      
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      
      &::-webkit-scrollbar-thumb {
          background-color: rgba(134, 144, 156, 0.35);
        border-radius: 3px;
        transition: background-color 0.2s;
        
        &:hover {
          background-color: rgba(134, 144, 156, 0.55);
        }
      }
    }
    
    scrollbar-width: none;
    -ms-overflow-style: none;
  }

  .el-scrollbar__bar {
    opacity: 0;
    transition: opacity 0.3s;
    
    .tags-view-container:hover & {
      opacity: 1;
    }
  }

  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all .3s cubic-bezier(.645, .045, .355, 1);
      transform-origin: 100% 50%;
      &:before {
        transform: scale(.6);
        display: inline-block;
        vertical-align: -3px;
      }
      &:hover {
        background-color: #b4bccc;
        color: #fff;
      }
    }
  }
}

/* 页签全屏模式样式 */
.main-container.fullscreen-mode {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  margin-left: 0 !important;
  transition: none !important;
}

.main-container.fullscreen-mode .fixed-header {
  display: block !important;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  width: 100% !important;
  z-index: 1000;
  transition: none !important;
}

.main-container.fullscreen-mode .fixed-header .navbar {
  display: none !important;
}

.main-container.fullscreen-mode .app-main {
  position: fixed;
  top: 38px;
  left: 0;
  right: 0;
  bottom: 0;
  margin: 0 !important;
  padding: 0 !important;
  height: calc(100vh - 38px) !important;
  min-height: calc(100vh - 38px) !important;
  overflow: auto;
}
</style>
