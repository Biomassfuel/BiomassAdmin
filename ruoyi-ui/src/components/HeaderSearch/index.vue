<template>
  <div class="header-search">
    <svg-icon class-name="search-icon" icon-class="search" @click.stop="click" />
    <el-dialog
      class="header-search-dialog"
      :visible.sync="show"
      width="600px"
      @close="close"
      @opened="onDialogOpened"
      :show-close="false"
      append-to-body
    >
      <el-input
        v-model="search"
        ref="headerSearchSelectRef"
        size="large"
        @input="querySearch"
        prefix-icon="el-icon-search"
        placeholder="菜单搜索，支持标题、URL模糊查询"
        clearable
        @keyup.enter.native="selectActiveResult"
        @keydown.up.native="navigateResult('up')"
        @keydown.down.native="navigateResult('down')"
      >
      </el-input>

      <div class="result-count" v-if="search && options.length > 0">
        找到 <strong>{{ options.length }}</strong> 个结果
      </div>

      <el-scrollbar wrap-class="right-scrollbar-wrapper">
        <div class="result-wrap">
          <template v-if="options.length > 0">
            <div
              class="search-item"
              v-for="(item, index) in options"
              :key="item.path"
              :class="{ 'is-active': index === activeIndex }"
              :style="activeStyle(index)"
              @mouseenter="activeIndex = index"
              @mouseleave="activeIndex = -1"
            >
              <div class="left">
                <svg-icon class="menu-icon" :icon-class="item.icon" />
              </div>
              <div class="search-info" @click="change(item)">
                <div class="menu-title" v-html="highlightText(item.title.join(' / '))"></div>
                <div class="menu-path" v-html="highlightText(item.path)"></div>
              </div>
              <svg-icon icon-class="enter" v-show="index === activeIndex" />
            </div>
          </template>

          <div class="empty-state" v-else-if="search && options.length === 0">
            <i class="el-icon-search empty-icon"></i>
            <p class="empty-text">未找到 "<strong>{{ search }}</strong>" 相关菜单</p>
            <p class="empty-tip">试试其他关键词或路径</p>
          </div>

        </div>
      </el-scrollbar>

      <div class="search-footer">
        <span class="shortcut-item">
          <kbd>↑</kbd><kbd>↓</kbd> 切换
        </span>
        <span class="shortcut-item">
          <kbd>↵</kbd> 选择
        </span>
        <span class="shortcut-item">
          <kbd>Esc</kbd> 关闭
        </span>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Fuse from 'fuse.js/dist/fuse.min.js'
import path from 'path'
import { isHttp } from '@/utils/validate'

export default {
  name: 'HeaderSearch',
  data() {
    return {
      search: '',
      options: [],
      searchPool: [],
      activeIndex: -1,
      show: false,
      fuse: undefined
    }
  },
  computed: {
    theme() {
      return this.$store.state.settings.theme
    },
    routes() {
      return this.$store.getters.defaultRoutes
    }
  },
  watch: {
    routes() {
      this.searchPool = this.generateRoutes(this.routes)
    },
    searchPool(list) {
      this.initFuse(list)
    }
  },
  mounted() {
    this.searchPool = this.generateRoutes(this.routes)
  },
  methods: {
    click() {
      this.show = !this.show
      if (this.show) {
        this.options = this.searchPool
      }
    },
    onDialogOpened() {
      this.$nextTick(() => {
        this.$refs.headerSearchSelectRef && this.$refs.headerSearchSelectRef.focus()
      })
    },
    close() {
      this.$refs.headerSearchSelectRef && this.$refs.headerSearchSelectRef.blur()
      this.search = ''
      this.options = this.searchPool
      this.show = false
      this.activeIndex = -1
    },
    change(val) {
      const p = val.path
      const query = val.query
      if (isHttp(val.path)) {
        // http(s):// 路径新窗口打开
        const pindex = p.indexOf('http')
        window.open(p.substr(pindex, p.length), '_blank')
      } else {
        if (query) {
          this.$router.push({ path: p, query: JSON.parse(query) })
        } else {
          this.$router.push(p)
        }
      }
      this.search = ''
      this.options = this.searchPool
      this.$nextTick(() => {
        this.show = false
      })
    },
    initFuse(list) {
      this.fuse = new Fuse(list, {
        shouldSort: true,
        threshold: 0.2,
        minMatchCharLength: 1,
        keys: [{
          name: 'title',
          weight: 0.7
        }, {
          name: 'path',
          weight: 0.3
        }]
      })
    },
    generateRoutes(routes, basePath = '/', prefixTitle = []) {
      let res = []
      for (const router of routes) {
        if (router.hidden) { continue }
        const data = {
          path: !isHttp(router.path) ? path.resolve(basePath, router.path) : router.path,
          title: [...prefixTitle],
          icon: ''
        }
        if (router.meta && router.meta.title) {
          data.title = [...data.title, router.meta.title]
          data.icon = router.meta.icon
          if (router.redirect !== 'noRedirect') {
            res.push(data)
          }
        }
        if (router.query) {
          data.query = router.query
        }
        if (router.children) {
          const tempRoutes = this.generateRoutes(router.children, data.path, data.title)
          if (tempRoutes.length >= 1) {
            res = [...res, ...tempRoutes]
          }
        }
      }
      return res
    },
    querySearch(query) {
      this.activeIndex = -1
      if (query !== '') {
        const q = query.toLowerCase()
        const pathMatches = this.searchPool.filter(item =>
          item.path.toLowerCase().includes(q)
        )
        const fuseMatches = this.fuse.search(query).map(item => item.item)
        const merged = [...pathMatches]
        fuseMatches.forEach(item => {
          if (!merged.find(m => m.path === item.path)) {
            merged.push(item)
          }
        })
        this.options = merged
      } else {
        this.options = this.searchPool
      }
    },
    activeStyle(index) {
      if (index !== this.activeIndex) return {}
      return {
        'background-color': this.theme,
        'color': '#fff'
      }
    },
    navigateResult(direction) {
      if (direction === 'up') {
        this.activeIndex = this.activeIndex <= 0 ? this.options.length - 1 : this.activeIndex - 1
      } else if (direction === 'down') {
        this.activeIndex = this.activeIndex >= this.options.length - 1 ? 0 : this.activeIndex + 1
      }
    },
    selectActiveResult() {
      if (this.options.length > 0 && this.activeIndex >= 0) {
        this.change(this.options[this.activeIndex])
      }
    },
    highlightText(text) {
      if (!text) return ''
      if (!this.search) return text
      const keyword = this.escapeRegExp(this.search)
      const reg = new RegExp(`(${keyword})`, 'gi')
      return text.replace(reg, '<span class="highlight">$1</span>')
    },
    escapeRegExp(str) {
      return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    }
  }
}
</script>

<style lang='scss' scoped>
::v-deep {
  .header-search-dialog {
    .el-dialog {
      overflow: hidden;
      border: 1px solid #edf1f7;
      border-radius: 10px;
      box-shadow: 0 22px 60px rgba(29, 33, 41, 0.16);
    }

    .el-dialog__body {
      padding: 16px 16px 0 !important;
    }
  }

  .el-dialog__header {
    display: none;
  }

  .highlight {
    color: #2468f2;
    font-weight: 600;
  }

  .is-active .highlight {
    color: rgba(255, 255, 255, 0.9);
    font-weight: 600;
  }
}

.header-search {
  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
    transition: color 0.18s ease;

    &:hover {
      color: #2468f2;
    }
  }
}

.result-count {
  padding: 10px 2px 0;
  font-size: 12px;
  color: #86909c;

  strong {
    color: #2468f2;
    font-weight: 600;
  }
}

.result-wrap {
  height: 310px;
  margin: 10px 0 0;

  .search-item {
    display: flex;
    align-items: center;
    min-height: 54px;
    padding: 8px 10px;
    border-radius: 8px;
    color: #1d2129;
    transition: background 0.15s, color 0.15s;

    .left {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      margin-right: 10px;
      border-radius: 8px;
      background: #f4f7fb;
      color: #4e5969;
      text-align: center;
      flex-shrink: 0;

      .menu-icon {
        width: 16px;
        height: 16px;
      }
    }

    .search-info {
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      flex: 1;
      width: 100%;
      overflow: hidden;
      padding-left: 0;
      margin-top: 0;

      .menu-title,
      .menu-path {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .menu-title {
        height: 22px;
        color: inherit;
        font-size: 13px;
        font-weight: 600;
        line-height: 22px;
      }

      .menu-path {
        height: 18px;
        color: #86909c;
        font-size: 12px;
        line-height: 18px;
      }
    }
  }

  .search-item:hover {
    background: #f4f7fb;
    cursor: pointer;
  }

  .search-item.is-active {
    .left {
      background: rgba(255, 255, 255, 0.18);
      color: #fff;
    }

    .menu-path {
      color: rgba(255, 255, 255, 0.78);
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;

    .empty-icon {
      font-size: 42px;
      color: #c9d6f2;
      margin-bottom: 14px;
    }

    .empty-text {
      font-size: 14px;
      color: #4e5969;
      margin: 0 0 6px;

      strong {
        color: #1d2129;
      }
    }

    .empty-tip {
      font-size: 12px;
      color: #86909c;
      margin: 0;
    }
  }
}

.search-footer {
  display: flex;
  align-items: center;
  gap: 18px;
  margin: 0 -16px;
  padding: 10px 16px;
  border-top: 1px solid #edf1f7;
  background: #f8fafc;
  color: #86909c;
  font-size: 12px;

  .shortcut-item {
    display: flex;
    align-items: center;
    gap: 5px;
  }

  kbd {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 20px;
    height: 20px;
    padding: 0 5px;
    border: 1px solid #e5e8ef;
    border-radius: 5px;
    background: #ffffff;
    color: #4e5969;
    font-size: 11px;
    font-family: inherit;
    line-height: 1;
    box-shadow: 0 1px 0 #dfe4ec;
  }
}

@media (max-width: 640px) {
  ::v-deep .header-search-dialog .el-dialog {
    width: calc(100vw - 32px) !important;
  }

  .search-footer {
    gap: 10px;
    flex-wrap: wrap;
  }
}
</style>
