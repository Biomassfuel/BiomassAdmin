<template>
  <div class="space-home-page">
    <section class="space-hero">
      <div class="hero-copy">
        <h1>{{ profile.title }}</h1>
        <p>{{ profile.desc }}</p>
      </div>
      <div v-if="profile.role !== 'unassigned'" class="hero-actions">
        <el-button
          v-for="action in primaryActions"
          :key="action.path"
          :type="action.primary ? 'primary' : 'default'"
          :icon="action.icon"
          size="small"
          @click="go(action.path)"
        >
          {{ action.label }}
        </el-button>
        <el-button icon="el-icon-refresh" size="small" :loading="refreshing" @click="refreshDashboard">刷新</el-button>
      </div>
    </section>

    <section v-if="profile.role === 'unassigned'" class="empty-role-card">
      <i class="el-icon-warning-outline"></i>
      <h2>未分配有效身份</h2>
      <p>请联系管理员分配空间预约管理员、老师或学生身份后使用系统。</p>
    </section>

    <template v-else>
      <el-row :gutter="16" class="metric-grid">
        <el-col v-for="item in metricCards" :key="item.label" :xs="24" :sm="12" :lg="6">
          <section class="space-metric-card" :class="{ clickable: item.path }" :style="metricStyle(item)" @click="go(item.path)">
            <div class="metric-top">
              <span>{{ item.label }}</span>
              <i :class="item.icon" />
            </div>
            <strong>{{ metricValue(item.panel, item.value) }}</strong>
            <p :class="{ error: panelError(item.panel) }">{{ metricHint(item.panel, item.hint) }}</p>
          </section>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :xs="24" :lg="15">
          <section class="space-card list-card">
            <div class="card-heading compact">
              <h3>{{ profile.mainTitle }}</h3>
              <el-button v-if="mainActionPath" type="text" @click="go(mainActionPath)">查看</el-button>
            </div>
            <panel-list :state="mainPanel" :empty-text="profile.mainEmpty">
              <div v-for="item in mainPanel.rows" :key="mainRowKey(item)" class="reservation-row">
                <div>
                  <strong>{{ mainRowTitle(item) }}</strong>
                  <p>{{ mainRowSubText(item) }}</p>
                </div>
                <el-tag size="mini" :type="mainRowTagType(item)" effect="plain">
                  {{ mainRowStatusText(item) }}
                </el-tag>
              </div>
            </panel-list>
          </section>
        </el-col>

        <el-col :xs="24" :lg="9">
          <section class="space-card shortcut-card">
            <div class="card-heading compact">
              <h3>快捷入口</h3>
            </div>
            <div class="shortcut-list">
              <button v-for="item in quickActions" :key="item.path" type="button" @click="go(item.path)">
                <i :class="item.icon" />
                <span>{{ item.label }}</span>
              </button>
            </div>
          </section>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col v-if="showPublicItemsPanel" :xs="24" :lg="12">
          <section class="space-card list-card">
            <div class="card-heading compact">
              <h3>近期公开占用</h3>
              <el-button v-if="publicListPath" type="text" @click="go(publicListPath)">所有预约</el-button>
            </div>
            <panel-list :state="panels.publicItems" empty-text="暂无公开占用">
              <div v-for="item in panels.publicItems.rows" :key="item.itemId" class="plain-row">
                <div>
                  <strong>{{ item.roomName || item.roomCode || '房间占用' }}</strong>
                  <p>{{ publicItemSubText(item) }}</p>
                </div>
                <el-tag size="mini" type="success" effect="plain">已通过</el-tag>
              </div>
            </panel-list>
          </section>
        </el-col>

        <el-col :xs="24" :lg="showPublicItemsPanel ? 12 : 24">
          <section class="space-card list-card">
            <div class="card-heading compact">
              <h3>房间资源</h3>
              <el-button v-if="roomListPath" type="text" @click="go(roomListPath)">查看</el-button>
            </div>
            <panel-list :state="panels.rooms" empty-text="暂无房间资源">
              <div v-for="item in panels.rooms.rows" :key="item.roomId" class="plain-row">
                <div>
                  <strong>{{ item.roomName || item.roomCode }}</strong>
                  <p>{{ roomSubText(item) }}</p>
                </div>
                <el-tag size="mini" :type="item.bookable === '0' ? 'success' : 'info'" effect="plain">
                  {{ item.bookable === '0' ? '可预约' : '不可预约' }}
                </el-tag>
              </div>
            </panel-list>
          </section>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script>
import request from '@/utils/request'
import { resolveHomeRole, hasPermi, hasAnyPermi } from '@/utils/home'

const emptyPanel = () => ({
  loading: false,
  error: '',
  rows: [],
  total: null
})

const PanelList = {
  name: 'SpaceHomePanelList',
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
    return h('div', { class: 'panel-list' }, this.$slots.default || [])
  }
}

export default {
  name: 'SpaceRoleHome',
  components: { PanelList },
  data() {
    return {
      refreshing: false,
      panels: {
        pending: emptyPanel(),
        cancelPending: emptyPanel(),
        reservations: emptyPanel(),
        myReservations: emptyPanel(),
        publicItems: emptyPanel(),
        rooms: emptyPanel()
      }
    }
  },
  computed: {
    roles() {
      return this.$store.getters.roles || []
    },
    permissions() {
      return this.$store.getters.permissions || []
    },
    profile() {
      const role = resolveHomeRole(this.roles)
      const profiles = {
        space_admin: {
          role,
          title: '空间预约管理首页',
          desc: '集中处理预约审核、取消审核与房间资源运行情况。',
          mainTitle: '待审核预约',
          mainEmpty: '暂无待审核预约',
          mainPanel: 'pending',
          mainPath: '/space-audit/pending'
        },
        space_teacher: {
          role,
          title: '教师预约首页',
          desc: '查看我的预约进度，快速发起单次或长期固定预约。',
          mainTitle: '我的预约',
          mainEmpty: '暂无我的预约',
          mainPanel: 'myReservations',
          mainPath: '/space-reservation/my'
        },
        space_student: {
          role,
          title: '学生空间查看首页',
          desc: '查看房间开放占用与已通过预约，便于安排学习与活动时间。',
          mainTitle: '近期公开占用',
          mainEmpty: '暂无公开占用',
          mainPanel: 'publicItems',
          mainPath: '/space-reservation/all'
        },
        unassigned: {
          role,
          title: '空间预约系统',
          desc: '当前账号还没有可用的空间预约身份。'
        }
      }
      return profiles[role] || profiles.unassigned
    },
    mainPanel() {
      return this.panels[this.profile.mainPanel] || emptyPanel()
    },
    showPublicItemsPanel() {
      return this.profile.role !== 'space_student'
    },
    mainActionPath() {
      const permissionMap = {
        pending: 'space:audit:list',
        myReservations: 'space:reservation:mine',
        publicItems: 'space:reservationItem:publicList'
      }
      const permission = permissionMap[this.profile.mainPanel]
      return permission && hasPermi(this.permissions, permission) ? this.profile.mainPath : ''
    },
    publicListPath() {
      return hasPermi(this.permissions, 'space:reservationItem:publicList') ? '/space-reservation/all' : ''
    },
    roomListPath() {
      if (this.profile.role === 'space_admin' && hasPermi(this.permissions, 'space:room:list')) {
        return '/space-room/list'
      }
      return hasPermi(this.permissions, 'space:reservationItem:publicList') ? '/space-reservation/occupancy' : ''
    },
    primaryActions() {
      if (this.profile.role === 'space_admin') {
        return this.filterActions([
          { label: '待审核', path: '/space-audit/pending', icon: 'el-icon-check', primary: true, permissions: ['space:audit:list'] },
          { label: '房间管理', path: '/space-room/list', icon: 'el-icon-office-building', permissions: ['space:room:list'] }
        ])
      }
      if (this.profile.role === 'space_teacher') {
        return this.filterActions([
          { label: '我要预约', path: '/space-reservation/apply', icon: 'el-icon-edit-outline', primary: true, permissions: ['space:reservation:add'] },
          { label: '我的预约', path: '/space-reservation/my', icon: 'el-icon-document', permissions: ['space:reservation:mine'] }
        ])
      }
      if (this.profile.role === 'space_student') {
        return this.filterActions([
          { label: '房间占用', path: '/space-reservation/occupancy', icon: 'el-icon-time', primary: true, permissions: ['space:reservationItem:publicList'] }
        ])
      }
      return []
    },
    quickActions() {
      const common = [
        { label: '房间占用查看', path: '/space-reservation/occupancy', icon: 'el-icon-time', permissions: ['space:reservationItem:publicList'] },
        { label: '所有预约', path: '/space-reservation/all', icon: 'el-icon-tickets', permissions: ['space:reservationItem:publicList'] }
      ]
      if (this.profile.role === 'space_admin') {
        return this.filterActions([
          { label: '待审核预约', path: '/space-audit/pending', icon: 'el-icon-s-check', permissions: ['space:audit:list'] },
          { label: '待取消审核', path: '/space-audit/cancel-pending', icon: 'el-icon-warning-outline', permissions: ['space:cancelAudit:list'] },
          { label: '预约记录', path: '/space-audit/record', icon: 'el-icon-document-copy', permissions: ['space:reservation:list'] },
          { label: '房间列表', path: '/space-room/list', icon: 'el-icon-office-building', permissions: ['space:room:list'] },
          ...common
        ])
      }
      if (this.profile.role === 'space_teacher') {
        return this.filterActions([
          { label: '我要预约', path: '/space-reservation/apply', icon: 'el-icon-edit-outline', permissions: ['space:reservation:add'] },
          { label: '长期固定预约', path: '/space-reservation/long', icon: 'el-icon-date', permissions: ['space:reservation:add'] },
          { label: '我的预约', path: '/space-reservation/my', icon: 'el-icon-user', permissions: ['space:reservation:mine'] },
          ...common
        ])
      }
      if (this.profile.role === 'space_student') {
        return this.filterActions(common)
      }
      return []
    },
    metricCards() {
      if (this.profile.role === 'space_admin') {
        return this.filterCards([
          { label: '待审核', panel: 'pending', value: this.panels.pending.total, hint: '待处理预约申请', icon: 'el-icon-s-check', accent: '#2563eb', soft: '#eff6ff', path: '/space-audit/pending', permissions: ['space:audit:list'] },
          { label: '待取消审核', panel: 'cancelPending', value: this.panels.cancelPending.total, hint: '待处理取消申请', icon: 'el-icon-warning-outline', accent: '#d97706', soft: '#fff7e8', path: '/space-audit/cancel-pending', permissions: ['space:cancelAudit:list'] },
          { label: '预约记录', panel: 'reservations', value: this.panels.reservations.total, hint: '全部预约申请', icon: 'el-icon-document-copy', accent: '#059669', soft: '#e8fff5', path: '/space-audit/record', permissions: ['space:reservation:list'] },
          { label: '房间资源', panel: 'rooms', value: this.panels.rooms.total, hint: '可查询房间数量', icon: 'el-icon-office-building', accent: '#7b61ff', soft: '#f2efff', path: this.roomListPath, permissions: ['space:reservationItem:publicList'] }
        ])
      }
      if (this.profile.role === 'space_teacher') {
        return this.filterCards([
          { label: '我的预约', panel: 'myReservations', value: this.panels.myReservations.total, hint: '我提交的预约', icon: 'el-icon-user', accent: '#2563eb', soft: '#eff6ff', path: '/space-reservation/my', permissions: ['space:reservation:mine'] },
          { label: '公开占用', panel: 'publicItems', value: this.panels.publicItems.total, hint: '近期已通过场次', icon: 'el-icon-time', accent: '#059669', soft: '#e8fff5', path: '/space-reservation/all', permissions: ['space:reservationItem:publicList'] },
          { label: '房间资源', panel: 'rooms', value: this.panels.rooms.total, hint: '可查看房间', icon: 'el-icon-office-building', accent: '#d97706', soft: '#fff7e8', path: '/space-reservation/occupancy', permissions: ['space:reservationItem:publicList'] },
          { label: '快捷申请', panel: 'myReservations', value: '预约', hint: '单次或长期固定预约', icon: 'el-icon-edit-outline', accent: '#7b61ff', soft: '#f2efff', path: '/space-reservation/apply', permissions: ['space:reservation:add'] }
        ])
      }
      return this.filterCards([
        { label: '公开占用', panel: 'publicItems', value: this.panels.publicItems.total, hint: '近期已通过场次', icon: 'el-icon-time', accent: '#2563eb', soft: '#eff6ff', path: '/space-reservation/all', permissions: ['space:reservationItem:publicList'] },
        { label: '房间资源', panel: 'rooms', value: this.panels.rooms.total, hint: '可查看房间', icon: 'el-icon-office-building', accent: '#059669', soft: '#e8fff5', path: '/space-reservation/occupancy', permissions: ['space:reservationItem:publicList'] },
        { label: '可预约房间', panel: 'rooms', value: this.bookableRoomCount, hint: '当前列表中可预约', icon: 'el-icon-circle-check', accent: '#d97706', soft: '#fff7e8', path: '/space-reservation/occupancy', permissions: ['space:reservationItem:publicList'] },
        { label: '今日安排', panel: 'publicItems', value: this.todayPublicCount, hint: '今日公开占用', icon: 'el-icon-date', accent: '#7b61ff', soft: '#f2efff', path: '/space-reservation/all', permissions: ['space:reservationItem:publicList'] }
      ])
    },
    bookableRoomCount() {
      if (this.panels.rooms.loading) return null
      return this.panels.rooms.rows.filter(item => item.bookable === '0').length
    },
    todayPublicCount() {
      if (this.panels.publicItems.loading) return null
      const today = this.formatDate(new Date())
      return this.panels.publicItems.rows.filter(item => item.bookingDate === today).length
    }
  },
  created() {
    if (this.profile.role !== 'unassigned') {
      this.refreshDashboard()
    }
  },
  methods: {
    async refreshDashboard() {
      const tasks = []
      if (this.profile.role === 'space_admin') {
        tasks.push(this.loadPanel('pending', '/space/reservation/pending/list', ['space:audit:list']))
        tasks.push(this.loadPanel('cancelPending', '/space/reservation/cancel-pending/list', ['space:cancelAudit:list']))
        tasks.push(this.loadPanel('reservations', '/space/reservation/list', ['space:reservation:list']))
      }
      if (this.profile.role === 'space_teacher') {
        tasks.push(this.loadPanel('myReservations', '/space/reservation/my/list', ['space:reservation:mine']))
      }
      tasks.push(this.loadPanel('publicItems', '/space/reservation/item/public/list', ['space:reservationItem:publicList'], { pageNum: 1, pageSize: 6 }))
      tasks.push(this.loadPanel('rooms', '/space/room/public/list', ['space:reservationItem:publicList'], { pageNum: 1, pageSize: 6 }))
      this.refreshing = true
      await Promise.all(tasks)
      this.refreshing = false
    },
    async loadPanel(key, url, permissions, params) {
      if (!hasAnyPermi(this.permissions, permissions)) {
        this.panels[key] = { ...emptyPanel(), error: '暂无权限' }
        return
      }
      this.setLoading(key)
      try {
        const res = await this.silentRequest({
          url,
          method: 'get',
          params: params || { pageNum: 1, pageSize: 6 }
        })
        this.panels[key] = {
          ...emptyPanel(),
          rows: res.rows || [],
          total: res.total || 0
        }
      } catch (error) {
        this.setError(key, error)
      }
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
      this.panels[key] = {
        ...emptyPanel(),
        error: this.normalizeError(error)
      }
    },
    normalizeError(error) {
      const message = String((error && error.message) || error || '')
      if (message.indexOf('403') > -1 || message.indexOf('权限') > -1) return '暂无权限'
      if (message.indexOf('401') > -1 || message.indexOf('会话') > -1) return '登录已过期'
      return '加载失败'
    },
    metricValue(panel, value) {
      if (this.panels[panel] && this.panels[panel].loading) return '...'
      if (this.panels[panel] && this.panels[panel].error) return '—'
      return value === null || value === undefined || value === '' ? '0' : value
    },
    metricHint(panel, fallback) {
      if (this.panels[panel] && this.panels[panel].loading) return '正在加载'
      return (this.panels[panel] && this.panels[panel].error) || fallback
    },
    panelError(panel) {
      return !!(this.panels[panel] && this.panels[panel].error)
    },
    metricStyle(item) {
      return {
        '--metric-accent': item.accent || '#2563eb',
        '--metric-soft': item.soft || '#eff6ff'
      }
    },
    filterActions(actions) {
      return actions.filter(action => !action.permissions || hasAnyPermi(this.permissions, action.permissions))
    },
    filterCards(cards) {
      return cards.filter(card => !card.permissions || hasAnyPermi(this.permissions, card.permissions))
    },
    mainRowKey(item) {
      return item.reservationId || item.itemId || item.roomId || item.reservationNo
    },
    mainRowTitle(item) {
      if (this.profile.mainPanel === 'publicItems') {
        return item.roomName || item.roomCode || '房间占用'
      }
      return item.title || item.reservationNo || '预约记录'
    },
    mainRowSubText(item) {
      if (this.profile.mainPanel === 'publicItems') {
        return this.publicItemSubText(item)
      }
      return this.reservationSubText(item)
    },
    mainRowTagType(item) {
      if (this.profile.mainPanel === 'publicItems') {
        return 'success'
      }
      return this.reservationTagType(item.status)
    },
    mainRowStatusText(item) {
      if (this.profile.mainPanel === 'publicItems') {
        return '已通过'
      }
      return this.reservationStatusText(item.status)
    },
    reservationStatusText(status) {
      const map = {
        '0': '草稿',
        '1': '待审核',
        '2': '已通过',
        '3': '部分通过',
        '4': '已驳回',
        '5': '已取消',
        '6': '已结束'
      }
      return map[status] || '已通过'
    },
    reservationTagType(status) {
      const map = {
        '1': 'warning',
        '2': 'success',
        '3': 'success',
        '4': 'danger',
        '5': 'info',
        '6': 'info'
      }
      return map[status] || 'success'
    },
    reservationSubText(item) {
      const applicant = item.applicantName ? `${item.applicantName} · ` : ''
      const time = item.submitTime || item.bookingDate || '-'
      return `${applicant}${item.reservationNo || '-'} · ${time}`
    },
    publicItemSubText(item) {
      const date = item.bookingDate || '-'
      const time = [item.startTime, item.endTime].filter(Boolean).join('-')
      return `${date} ${time || ''} · ${item.title || item.reservationNo || '公开占用'}`
    },
    roomSubText(item) {
      const capacity = item.capacityDesc || (item.capacityMax ? `${item.capacityMax}人以内` : '-')
      return `${item.buildingName || '-'} ${item.floorNo || ''} · ${item.roomType || '-'} · ${capacity}`
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      return `${year}-${month}-${day}`
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
.space-home-page {
  min-height: 100%;
  padding: 24px;
  background: #f3f4f6;
}

.space-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding: 22px 24px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background:
    linear-gradient(135deg, rgba(63, 143, 77, 0.10), rgba(37, 99, 235, 0.05) 48%, rgba(255, 255, 255, 0) 78%),
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
}

.hero-actions {
  display: flex;
  flex: 0 0 auto;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.empty-role-card,
.space-card,
.space-metric-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.empty-role-card {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  min-height: 320px;
  color: #4b5563;
  text-align: center;

  i {
    color: #d97706;
    font-size: 34px;
  }

  h2 {
    margin: 16px 0 8px;
    color: #111827;
    font-size: 20px;
  }

  p {
    margin: 0;
    font-size: 13px;
  }
}

.metric-grid {
  margin-bottom: 16px;
}

.space-metric-card {
  min-height: 124px;
  margin-bottom: 16px;
  padding: 18px 18px 16px;
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

.space-card {
  margin-bottom: 16px;
  padding: 18px;
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

  &.compact {
    align-items: center;
  }
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

.reservation-row,
.plain-row {
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
    max-width: 420px;
    margin: 3px 0 0;
    overflow: hidden;
    color: #6b7280;
    font-size: 12px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.shortcut-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  min-height: 214px;
  align-content: start;

  button {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    min-height: 42px;
    padding: 10px 12px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    background: #f8fafc;
    color: #111827;
    text-align: left;
    font-size: 13px;
    cursor: pointer;
    transition: border-color 0.18s ease, background 0.18s ease;

    &:hover {
      border-color: #bfdbfe;
      background: #eff6ff;
      color: #2563eb;
    }
  }
}

@media (max-width: 768px) {
  .space-home-page {
    padding: 16px;
  }

  .space-hero {
    align-items: flex-start;
    flex-direction: column;
    padding: 18px;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
