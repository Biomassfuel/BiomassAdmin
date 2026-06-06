<template>
  <div class="dashboard-page">
    <div class="saas-page-header dashboard-header">
      <div>
        <h1 class="saas-page-title">运营驾驶舱</h1>
        <div class="saas-page-desc">聚合业务概览、系统状态、待办事项与最近操作，提供生产后台的首屏工作入口。</div>
      </div>
      <div class="dashboard-actions">
        <el-button icon="el-icon-refresh" plain size="small">刷新数据</el-button>
        <el-button type="primary" icon="el-icon-plus" size="small">新建任务</el-button>
      </div>
    </div>

    <el-row :gutter="20" class="metric-grid">
      <el-col v-for="item in metrics" :key="item.label" :xs="24" :sm="12" :lg="6">
        <section class="metric-card">
          <div class="metric-icon" :class="item.type">
            <i :class="item.icon" />
          </div>
          <div class="metric-content">
            <span>{{ item.label }}</span>
            <strong>{{ item.value }}</strong>
            <em :class="{ down: item.trendType === 'down' }">{{ item.trend }}</em>
          </div>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="16">
        <section class="saas-card chart-card">
          <div class="card-heading">
            <div>
              <h3>业务趋势</h3>
              <p>近 7 日处理量与完成率</p>
            </div>
            <el-radio-group v-model="trendRange" size="mini">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
            </el-radio-group>
          </div>
          <line-chart :chart-data="lineChartData" height="320px" />
        </section>
      </el-col>

      <el-col :xs="24" :lg="8">
        <section class="saas-card health-card">
          <div class="card-heading compact">
            <div>
              <h3>系统健康</h3>
              <p>关键服务运行状态</p>
            </div>
          </div>
          <div v-for="item in health" :key="item.name" class="health-item">
            <div>
              <strong>{{ item.name }}</strong>
              <span>{{ item.desc }}</span>
            </div>
            <el-tag :type="item.type" size="mini">{{ item.status }}</el-tag>
          </div>
        </section>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-lower">
      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>待办事项</h3>
            <el-button type="text">查看全部</el-button>
          </div>
          <div v-for="item in todos" :key="item.title" class="todo-item">
            <span :class="item.level" />
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </section>
      </el-col>

      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>通知公告</h3>
            <el-button type="text">管理公告</el-button>
          </div>
          <div v-for="item in notices" :key="item.title" class="notice-item">
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.time }}</p>
            </div>
            <el-tag size="mini" effect="plain">{{ item.type }}</el-tag>
          </div>
        </section>
      </el-col>

      <el-col :xs="24" :lg="8">
        <section class="saas-card list-card">
          <div class="card-heading compact">
            <h3>最近操作</h3>
            <el-button type="text">审计日志</el-button>
          </div>
          <div v-for="item in activities" :key="item.action" class="activity-item">
            <div class="activity-dot" />
            <div>
              <strong>{{ item.action }}</strong>
              <p>{{ item.operator }} · {{ item.time }}</p>
            </div>
          </div>
        </section>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import LineChart from './dashboard/LineChart'

const lineChartData = {
  expectedData: [96, 118, 132, 145, 158, 176, 188],
  actualData: [88, 102, 126, 138, 149, 168, 182]
}

export default {
  name: 'Index',
  components: { LineChart },
  data() {
    return {
      trendRange: 'week',
      lineChartData,
      metrics: [
        { label: '今日处理量', value: '12,860', trend: '+12.4%', type: 'blue', icon: 'el-icon-data-line' },
        { label: '待办任务', value: '38', trend: '-6.8%', trendType: 'down', type: 'orange', icon: 'el-icon-s-order' },
        { label: '在线用户', value: '126', trend: '+8.2%', type: 'green', icon: 'el-icon-user' },
        { label: '异常告警', value: '4', trend: '-18.0%', trendType: 'down', type: 'red', icon: 'el-icon-warning-outline' }
      ],
      health: [
        { name: '权限服务', desc: 'RBAC 与动态菜单正常', status: '正常', type: 'success' },
        { name: '缓存服务', desc: 'Redis 连接与命中率稳定', status: '正常', type: 'success' },
        { name: '定时任务', desc: '调度队列有 2 个待执行任务', status: '关注', type: 'warning' },
        { name: '审计日志', desc: '操作日志写入正常', status: '正常', type: 'success' }
      ],
      todos: [
        { title: '复核新增角色权限', desc: '2 个角色等待授权确认', level: 'high' },
        { title: '处理导入失败记录', desc: '用户导入存在 3 条异常数据', level: 'medium' },
        { title: '同步代码生成配置', desc: '1 张表结构发生变化', level: 'low' }
      ],
      notices: [
        { title: '平台视觉体系已升级', type: '产品', time: '今天 09:30' },
        { title: '生产环境接口文档入口已隐藏', type: '安全', time: '昨天 18:20' },
        { title: '请定期复核管理员权限', type: '权限', time: '周一 10:12' }
      ],
      activities: [
        { action: '更新菜单结构', operator: 'admin', time: '5 分钟前' },
        { action: '生成业务模块代码', operator: 'developer', time: '26 分钟前' },
        { action: '导出登录日志', operator: 'audit', time: '1 小时前' }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100%;
  padding: 24px;
  background: #f5f7fb;
}

.dashboard-header {
  align-items: center;
}

.dashboard-actions {
  display: flex;
  gap: 10px;
}

.metric-grid {
  margin-bottom: 20px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 118px;
  padding: 22px;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(31, 35, 41, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 28px rgba(31, 35, 41, 0.1);
  }
}

.metric-icon {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  font-size: 22px;

  &.blue { color: #1677ff; background: #e8f2ff; }
  &.green { color: #00a870; background: #e8fff5; }
  &.orange { color: #f5a524; background: #fff7e8; }
  &.red { color: #f53f3f; background: #fff0f0; }
}

.metric-content {
  span,
  em {
    display: block;
    color: #646a73;
    font-size: 13px;
    font-style: normal;
  }

  strong {
    display: block;
    margin: 6px 0;
    color: #1f2329;
    font-size: 26px;
    font-weight: 700;
    line-height: 32px;
  }

  em {
    color: #00a870;

    &.down {
      color: #f53f3f;
    }
  }
}

.chart-card,
.health-card,
.list-card {
  margin-bottom: 20px;
  padding: 20px;
}

.card-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  h3 {
    margin: 0;
    color: #1f2329;
    font-size: 16px;
    font-weight: 600;
    line-height: 24px;
  }

  p {
    margin: 4px 0 0;
    color: #8f959e;
    font-size: 13px;
  }

  &.compact {
    align-items: center;
    margin-bottom: 14px;
  }
}

.health-item,
.notice-item,
.activity-item,
.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #edf1f7;

  &:last-child {
    border-bottom: none;
  }

  strong {
    display: block;
    color: #1f2329;
    font-size: 14px;
    font-weight: 600;
  }

  p,
  span {
    display: block;
    margin: 4px 0 0;
    color: #8f959e;
    font-size: 12px;
  }
}

.health-item,
.notice-item {
  justify-content: space-between;
}

.todo-item > span {
  width: 8px;
  height: 34px;
  border-radius: 999px;
  background: #1677ff;

  &.high { background: #f53f3f; }
  &.medium { background: #f5a524; }
  &.low { background: #00a870; }
}

.activity-dot {
  width: 10px;
  height: 10px;
  border: 2px solid #1677ff;
  border-radius: 50%;
  background: #fff;
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 16px;
  }

  .dashboard-header {
    align-items: flex-start;
  }

  .dashboard-actions {
    margin-top: 12px;
  }
}
</style>
