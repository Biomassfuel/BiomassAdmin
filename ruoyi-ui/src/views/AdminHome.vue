<template>
  <div class="dashboard-page">
    <div class="dashboard-hero">
      <div class="hero-copy">
        <h1>BiomassAdmin</h1>
        <p>系统运行概览、公告与最近操作</p>
      </div>
      <el-button icon="el-icon-refresh" size="small" :loading="refreshing" @click="refreshDashboard">刷新</el-button>
    </div>

    <el-row :gutter="16" class="metric-grid">
      <el-col v-for="item in metricCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <section class="metric-card" :class="{ clickable: item.path }" :style="metricStyle(item)" @click="go(item.path)">
          <div class="metric-top">
            <span>{{ item.label }}</span>
            <i :class="item.icon" />
          </div>
          <strong>{{ item.value }}</strong>
          <p :class="{ error: item.error }">{{ item.hint }}</p>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="15">
        <section class="saas-card overview-card">
          <div class="card-heading">
            <div>
              <h3>运行状态</h3>
              <p>来自服务监控与缓存监控接口</p>
            </div>
            <span class="status-dot" :class="{ muted: hasSystemError }"></span>
          </div>

          <div v-if="panelLoading('server') || panelLoading('cache')" class="panel-state">
            <i class="el-icon-loading"></i>
            <span>正在加载运行状态</span>
          </div>
          <div v-else class="system-grid">
            <div v-for="item in systemStatus" :key="item.label" class="system-item">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
              <em :class="item.level">{{ item.hint }}</em>
            </div>
          </div>
        </section>
      </el-col>

      <el-col :xs="24" :lg="9">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>通知公告</h3>
            <el-button type="text" @click="go('/system/notice')">管理</el-button>
          </div>
          <panel-list :state="panels.notice" empty-text="暂无公告">
            <div v-for="item in panels.notice.rows" :key="item.noticeId" class="notice-row">
              <div>
                <strong>{{ item.noticeTitle }}</strong>
                <p>{{ item.createTime || '暂无时间' }}</p>
              </div>
              <el-tag size="mini" :type="item.isRead ? 'info' : 'warning'" effect="plain">
                {{ item.isRead ? '已读' : '未读' }}
              </el-tag>
            </div>
          </panel-list>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>定时任务</h3>
            <el-button type="text" @click="go('/monitor/job')">查看</el-button>
          </div>
          <panel-list :state="panels.job" empty-text="暂无任务">
            <div v-for="item in panels.job.rows" :key="item.jobId" class="plain-row">
              <div>
                <strong>{{ item.jobName }}</strong>
                <p>{{ item.invokeTarget }}</p>
              </div>
              <el-tag size="mini" :type="item.status === '0' ? 'success' : 'info'" effect="plain">
                {{ item.status === '0' ? '运行' : '暂停' }}
              </el-tag>
            </div>
          </panel-list>
        </section>
      </el-col>

      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>最近操作</h3>
            <el-button type="text" @click="go('/system/log/operlog')">审计</el-button>
          </div>
          <panel-list :state="panels.operlog" empty-text="暂无操作日志">
            <div v-for="item in panels.operlog.rows" :key="item.operId" class="timeline-row">
              <span class="timeline-dot"></span>
              <div>
                <strong>{{ item.title || item.operName || '系统操作' }}</strong>
                <p>{{ item.operName || '-' }} · {{ item.operTime || '-' }}</p>
              </div>
            </div>
          </panel-list>
        </section>
      </el-col>

      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>登录记录</h3>
            <el-button type="text" @click="go('/system/log/logininfor')">日志</el-button>
          </div>
          <panel-list :state="panels.logininfor" empty-text="暂无登录记录">
            <div v-for="item in panels.logininfor.rows" :key="item.infoId" class="plain-row">
              <div>
                <strong>{{ item.userName || '未知用户' }}</strong>
                <p>{{ item.ipaddr || '-' }} · {{ item.loginTime || '-' }}</p>
              </div>
              <el-tag size="mini" :type="item.status === '0' ? 'success' : 'danger'" effect="plain">
                {{ item.status === '0' ? '成功' : '失败' }}
              </el-tag>
            </div>
          </panel-list>
        </section>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'

const emptyPanel = () => ({
  loading: false,
  error: '',
  rows: [],
  total: null,
  data: null,
  unreadCount: 0
})

const PanelList = {
  name: 'PanelList',
  props: {
    state: {
      type: Object,
      required: true
    },
    emptyText: {
      type: String,
      default: '暂无数据'
    }
  },
  render(h) {
    if (this.state.loading) {
      return h('div', { class: 'panel-state' }, [
        h('i', { class: 'el-icon-loading' }),
        h('span', '正在加载')
      ])
    }
    if (this.state.error) {
      return h('div', { class: 'panel-state muted' }, [
        h('i', { class: 'el-icon-warning-outline' }),
        h('span', this.state.error)
      ])
    }
    const rows = Array.isArray(this.state.rows) ? this.state.rows : []
    if (!rows.length) {
      return h('div', { class: 'panel-state muted' }, [
        h('i', { class: 'el-icon-document' }),
        h('span', this.emptyText)
      ])
    }
    const children = this.$slots.default || []
    return h('div', { class: 'panel-list' }, children)
  }
}

export default {
  name: 'AdminHome',
  components: { PanelList },
  data() {
    return {
      refreshing: false,
      panels: {
        notice: emptyPanel(),
        server: emptyPanel(),
        cache: emptyPanel(),
        online: emptyPanel(),
        job: emptyPanel(),
        operlog: emptyPanel(),
        logininfor: emptyPanel()
      }
    }
  },
  computed: {
    metricCards() {
      return [
        {
          label: '在线用户',
          value: this.metricValue('online', this.panels.online.total),
          hint: this.metricHint('online', '当前登录会话'),
          error: !!this.panels.online.error,
          icon: 'el-icon-user',
          accent: '#2563eb',
          soft: '#eff6ff',
          path: '/monitor/online'
        },
        {
          label: '未读公告',
          value: this.metricValue('notice', this.panels.notice.unreadCount),
          hint: this.metricHint('notice', '来自通知公告'),
          error: !!this.panels.notice.error,
          icon: 'el-icon-message',
          accent: '#059669',
          soft: '#e8fff5'
        },
        {
          label: '定时任务',
          value: this.metricValue('job', this.jobSummary),
          hint: this.metricHint('job', '启用 / 总数'),
          error: !!this.panels.job.error,
          icon: 'el-icon-alarm-clock',
          accent: '#d97706',
          soft: '#fff7e8',
          path: '/monitor/job'
        },
        {
          label: 'Redis Key',
          value: this.metricValue('cache', this.panels.cache.data && this.panels.cache.data.dbSize),
          hint: this.metricHint('cache', '当前缓存数量'),
          error: !!this.panels.cache.error,
          icon: 'el-icon-coin',
          accent: '#7b61ff',
          soft: '#f2efff',
          path: '/monitor/cache'
        }
      ]
    },
    jobSummary() {
      if (this.panels.job.total === null) return null
      const active = this.panels.job.rows.filter(item => item.status === '0').length
      return `${active}/${this.panels.job.total}`
    },
    hasSystemError() {
      return !!this.panels.server.error || !!this.panels.cache.error
    },
    systemStatus() {
      const server = this.panels.server.data || {}
      const cache = this.panels.cache.data || {}
      const cpuUsed = this.toNumber(server.cpu && server.cpu.used)
      const memUsage = this.toNumber(server.mem && server.mem.usage)
      const jvmUsage = this.toNumber(server.jvm && server.jvm.usage)
      const disk = Array.isArray(server.sysFiles) ? server.sysFiles[0] : null
      const diskUsage = this.toNumber(disk && disk.usage)
      return [
        {
          label: 'CPU',
          value: this.percentValue(cpuUsed),
          hint: server.cpu ? `${server.cpu.cpuNum || '-'} 核心` : this.panelHint('server'),
          level: this.level(cpuUsed)
        },
        {
          label: '内存',
          value: this.percentValue(memUsage),
          hint: server.mem ? `${server.mem.used || '-'}G / ${server.mem.total || '-'}G` : this.panelHint('server'),
          level: this.level(memUsage)
        },
        {
          label: 'JVM',
          value: this.percentValue(jvmUsage),
          hint: server.jvm ? `运行 ${server.jvm.runTime || '-'}` : this.panelHint('server'),
          level: this.level(jvmUsage)
        },
        {
          label: '磁盘',
          value: this.percentValue(diskUsage),
          hint: disk ? disk.dirName : this.panelHint('server'),
          level: this.level(diskUsage)
        },
        {
          label: 'Redis',
          value: cache.info ? (cache.info.used_memory_human || '-') : '—',
          hint: cache.info ? `${cache.info.redis_version || '-'} · ${cache.info.connected_clients || 0} 客户端` : this.panelHint('cache'),
          level: cache.info ? 'good' : 'muted'
        }
      ]
    }
  },
  created() {
    this.refreshDashboard()
  },
  methods: {
    async refreshDashboard() {
      this.refreshing = true
      await Promise.all([
        this.loadNotice(),
        this.loadServer(),
        this.loadCache(),
        this.loadOnline(),
        this.loadJob(),
        this.loadOperlog(),
        this.loadLogininfor()
      ])
      this.refreshing = false
    },
    silentRequest(config) {
      return request({
        ...config,
        headers: {
          ...(config.headers || {}),
          hideError: true
        }
      })
    },
    setLoading(key) {
      this.panels[key].loading = true
      this.panels[key].error = ''
    },
    setError(key, error) {
      this.panels[key].loading = false
      this.panels[key].error = this.normalizeError(error)
      this.panels[key].rows = []
      this.panels[key].total = null
      this.panels[key].data = null
    },
    normalizeError(error) {
      const message = String((error && error.message) || error || '')
      if (message.indexOf('403') > -1 || message.indexOf('权限') > -1) {
        return '暂无权限'
      }
      if (message.indexOf('401') > -1 || message.indexOf('会话') > -1) {
        return '登录已过期'
      }
      return '加载失败'
    },
    async loadNotice() {
      const key = 'notice'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/system/notice/listTop', method: 'get' })
        this.panels[key] = {
          ...emptyPanel(),
          rows: Array.isArray(res.data) ? res.data : [],
          total: Array.isArray(res.data) ? res.data.length : 0,
          unreadCount: res.unreadCount || 0
        }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadServer() {
      const key = 'server'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/server', method: 'get' })
        this.panels[key] = { ...emptyPanel(), data: res.data || {} }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadCache() {
      const key = 'cache'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/cache', method: 'get' })
        this.panels[key] = { ...emptyPanel(), data: res.data || {} }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadOnline() {
      const key = 'online'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/online/list', method: 'get', params: { pageNum: 1, pageSize: 5 } })
        this.panels[key] = { ...emptyPanel(), rows: res.rows || [], total: res.total || 0 }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadJob() {
      const key = 'job'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/job/list', method: 'get', params: { pageNum: 1, pageSize: 5 } })
        this.panels[key] = { ...emptyPanel(), rows: res.rows || [], total: res.total || 0 }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadOperlog() {
      const key = 'operlog'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/operlog/list', method: 'get', params: { pageNum: 1, pageSize: 5 } })
        this.panels[key] = { ...emptyPanel(), rows: res.rows || [], total: res.total || 0 }
      } catch (error) {
        this.setError(key, error)
      }
    },
    async loadLogininfor() {
      const key = 'logininfor'
      this.setLoading(key)
      try {
        const res = await this.silentRequest({ url: '/monitor/logininfor/list', method: 'get', params: { pageNum: 1, pageSize: 5 } })
        this.panels[key] = { ...emptyPanel(), rows: res.rows || [], total: res.total || 0 }
      } catch (error) {
        this.setError(key, error)
      }
    },
    metricValue(key, value) {
      if (this.panels[key].loading) return '...'
      if (this.panels[key].error) return '—'
      return value === null || value === undefined || value === '' ? '0' : value
    },
    metricHint(key, fallback) {
      if (this.panels[key].loading) return '正在加载'
      return this.panels[key].error || fallback
    },
    metricStyle(item) {
      return {
        '--metric-accent': item.accent || '#2563eb',
        '--metric-soft': item.soft || '#eff6ff'
      }
    },
    panelLoading(key) {
      return this.panels[key] && this.panels[key].loading
    },
    panelHint(key) {
      return this.panels[key].error || '暂无数据'
    },
    percentValue(value) {
      return value === null || value === undefined || Number.isNaN(value) ? '—' : `${value}%`
    },
    toNumber(value) {
      if (value === null || value === undefined || value === '') return null
      const number = Number(value)
      return Number.isNaN(number) ? null : number
    },
    level(value) {
      if (value === null || value === undefined) return 'muted'
      if (value >= 85) return 'danger'
      if (value >= 70) return 'warning'
      return 'good'
    },
    go(path) {
      if (path) {
        this.$router.push(path).catch(() => {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100%;
  padding: 24px;
  background: #f3f4f6;
}

.dashboard-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding: 22px 24px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.07), rgba(5, 150, 105, 0.04) 42%, rgba(255, 255, 255, 0) 78%),
    #ffffff;
  color: #111827;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);

  .hero-copy {
    min-width: 0;
  }

  h1 {
    margin: 0;
    color: #111827;
    font-size: 24px;
    font-weight: 750;
    line-height: 32px;
  }

  p {
    margin: 6px 0 0;
    color: #4b5563;
    font-size: 13px;
    line-height: 20px;
  }

  ::v-deep .el-button {
    flex: 0 0 auto;
    border-color: #bfdbfe;
    color: #2563eb;
    background: #ffffff;

    &:hover,
    &:focus {
      border-color: #2563eb;
      background: #eff6ff;
    }
  }
}

.metric-grid {
  margin-bottom: 16px;
}

.metric-card {
  min-height: 124px;
  margin-bottom: 16px;
  padding: 18px 18px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;

  &.clickable {
    cursor: pointer;
  }

  &:hover {
    border-color: rgba(37, 99, 235, 0.28);
    box-shadow: 0 8px 24px rgba(29, 33, 41, 0.08);
    transform: translateY(-1px);
  }

  strong {
    display: block;
    margin: 14px 0 6px;
    color: #111827;
    font-size: 28px;
    font-weight: 750;
    line-height: 34px;
  }

  p {
    margin: 0;
    color: #6b7280;
    font-size: 12px;
    line-height: 18px;

    &.error {
      color: #dc2626;
    }
  }
}

.metric-top {
  display: flex;
  align-items: center;
  justify-content: space-between;

  span {
    color: #4b5563;
    font-size: 13px;
    font-weight: 600;
  }

  i {
    display: inline-grid;
    place-items: center;
    width: 34px;
    height: 34px;
    border-radius: 8px;
    color: var(--metric-accent);
    background: var(--metric-soft);
    font-size: 17px;
  }
}

.overview-card,
.list-card {
  margin-bottom: 16px;
  padding: 18px;
  border-radius: 8px;
}

.card-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    color: #111827;
    font-size: 15px;
    font-weight: 650;
    line-height: 22px;
  }

  p {
    margin: 4px 0 0;
    color: #6b7280;
    font-size: 12px;
    line-height: 18px;
  }

  &.compact {
    align-items: center;
  }
}

.status-dot {
  width: 9px;
  height: 9px;
  margin-top: 6px;
  border-radius: 50%;
  background: #059669;

  &.muted {
    background: #d97706;
  }
}

.system-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.system-item {
  min-height: 104px;
  padding: 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;

  span,
  em {
    display: block;
    color: #6b7280;
    font-size: 12px;
    font-style: normal;
    line-height: 18px;
  }

  strong {
    display: block;
    margin: 10px 0 6px;
    color: #111827;
    font-size: 22px;
    font-weight: 750;
  }

  em.good { color: #059669; }
  em.warning { color: #d97706; }
  em.danger { color: #dc2626; }
  em.muted { color: #6b7280; }
}

.panel-list {
  min-height: 214px;
}

.panel-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 214px;
  color: #4b5563;
  font-size: 13px;

  &.muted {
    color: #6b7280;
  }
}

.notice-row,
.plain-row,
.timeline-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;

  &:last-child {
    border-bottom: none;
  }

  strong {
    display: block;
    color: #111827;
    font-size: 13px;
    font-weight: 600;
    line-height: 20px;
  }

  p {
    max-width: 260px;
    margin: 3px 0 0;
    overflow: hidden;
    color: #6b7280;
    font-size: 12px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.timeline-row {
  justify-content: flex-start;
}

.timeline-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #2563eb;
  box-shadow: 0 0 0 4px #eff6ff;
}

@media (max-width: 1200px) {
  .system-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
  }

  .dashboard-hero {
    align-items: flex-start;
    flex-direction: column;
    padding: 18px;
  }

  .system-grid {
    grid-template-columns: 1fr;
  }
}
</style>
