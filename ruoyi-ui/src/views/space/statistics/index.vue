<template>
  <div class="statistics-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="78px" class="filter-bar">
      <el-form-item label="统计日期">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          clearable
          @change="handleDateChange"
        />
      </el-form-item>
      <el-form-item label="房间编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房间编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房间名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['space:statistics:export']">导出</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="12" :sm="8" :lg="4" v-for="item in summaryCards" :key="item.label">
        <div class="summary-card">
          <div class="summary-card__icon" :class="item.type">
            <i :class="item.icon" />
          </div>
          <div class="summary-card__content">
            <div class="summary-card__label">{{ item.label }}</div>
            <div class="summary-card__value">{{ item.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-card__header">
            <span>预约趋势</span>
          </div>
          <div ref="trendChart" class="chart" v-loading="dashboardLoading" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-card__header">
            <span>成功/驳回占比</span>
          </div>
          <div ref="statusChart" class="chart" v-loading="dashboardLoading" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="panel-card table-card">
      <div slot="header" class="panel-card__header">
        <span>房间预约数据统计</span>
      </div>
      <el-table v-loading="listLoading" :data="roomList">
        <el-table-column label="房间编号" prop="roomCode" align="center" width="120" />
        <el-table-column label="房间名称" prop="roomName" align="center" min-width="160" :show-overflow-tooltip="true" />
        <el-table-column label="预约频次" prop="reservationCount" align="center" width="120" sortable />
        <el-table-column label="占用率" align="center" width="120" sortable>
          <template slot-scope="scope">{{ formatRate(scope.row.occupancyRate) }}</template>
        </el-table-column>
        <el-table-column label="预约成功" prop="successCount" align="center" width="120" sortable />
        <el-table-column label="预约驳回" prop="rejectCount" align="center" width="120" sortable />
        <el-table-column label="占用时长" align="center" width="130" sortable>
          <template slot-scope="scope">{{ scope.row.occupiedHours || 0 }}小时</template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStatisticsDashboard, listStatistics } from '@/api/space/statistics'

export default {
  name: 'SpaceStatistics',
  data() {
    return {
      dashboardLoading: false,
      listLoading: false,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        roomCode: null,
        roomName: null
      },
      total: 0,
      summary: {},
      roomList: [],
      dailyTrend: [],
      trendChart: null,
      statusChart: null
    }
  },
  computed: {
    summaryCards() {
      return [
        { label: '预约频次', value: this.summary.reservationCount || 0, icon: 'el-icon-date', type: 'primary' },
        { label: '占用率', value: this.formatRate(this.summary.occupancyRate), icon: 'el-icon-data-line', type: 'success' },
        { label: '预约成功', value: this.summary.successCount || 0, icon: 'el-icon-circle-check', type: 'teal' },
        { label: '预约驳回', value: this.summary.rejectCount || 0, icon: 'el-icon-circle-close', type: 'danger' },
        { label: '待处理', value: this.summary.pendingCount || 0, icon: 'el-icon-time', type: 'warning' },
        { label: '涉及房间', value: this.summary.roomCount || 0, icon: 'el-icon-office-building', type: 'info' }
      ]
    }
  },
  created() {
    this.initDefaultDate()
    this.getDashboard()
    this.getList()
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    if (this.trendChart) this.trendChart.dispose()
    if (this.statusChart) this.statusChart.dispose()
  },
  methods: {
    initDefaultDate() {
      const end = new Date()
      const start = new Date()
      start.setDate(end.getDate() - 29)
      this.dateRange = [this.formatDate(start), this.formatDate(end)]
      this.handleDateChange(this.dateRange)
    },
    getDashboard() {
      this.dashboardLoading = true
      getStatisticsDashboard(this.dashboardQuery()).then(response => {
        const data = response.data || {}
        this.summary = data.summary || {}
        this.dailyTrend = data.dailyTrend || []
        this.dashboardLoading = false
        this.$nextTick(() => {
          this.renderTrendChart()
          this.renderStatusChart()
        })
      }).catch(() => {
        this.dashboardLoading = false
      })
    },
    getList() {
      this.listLoading = true
      listStatistics(this.queryParams).then(response => {
        this.roomList = response.rows || []
        this.total = response.total || 0
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })
    },
    dashboardQuery() {
      const { pageNum, pageSize, ...query } = this.queryParams
      return query
    },
    renderTrendChart() {
      if (!this.trendChart) {
        this.trendChart = echarts.init(this.$refs.trendChart)
      }
      const dates = this.dailyTrend.map(item => item.statDate)
      this.trendChart.setOption({
        color: ['#2f80ed', '#27ae60', '#eb5757'],
        tooltip: { trigger: 'axis' },
        legend: { top: 0, data: ['预约频次', '预约成功', '预约驳回'] },
        grid: { top: 44, left: 24, right: 18, bottom: 24, containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: dates },
        yAxis: { type: 'value', minInterval: 1 },
        series: [
          { name: '预约频次', type: 'line', smooth: true, data: this.dailyTrend.map(item => item.reservationCount || 0) },
          { name: '预约成功', type: 'line', smooth: true, data: this.dailyTrend.map(item => item.successCount || 0) },
          { name: '预约驳回', type: 'line', smooth: true, data: this.dailyTrend.map(item => item.rejectCount || 0) }
        ]
      })
      this.trendChart.resize()
    },
    renderStatusChart() {
      if (!this.statusChart) {
        this.statusChart = echarts.init(this.$refs.statusChart)
      }
      this.statusChart.setOption({
        color: ['#27ae60', '#eb5757', '#f2c94c'],
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          name: '预约状态',
          type: 'pie',
          radius: ['48%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          data: [
            { name: '预约成功', value: this.summary.successCount || 0 },
            { name: '预约驳回', value: this.summary.rejectCount || 0 },
            { name: '待处理', value: this.summary.pendingCount || 0 }
          ]
        }]
      })
      this.statusChart.resize()
    },
    resizeCharts() {
      if (this.trendChart) this.trendChart.resize()
      if (this.statusChart) this.statusChart.resize()
    },
    handleDateChange(value) {
      this.queryParams.startDate = value && value.length ? value[0] : null
      this.queryParams.endDate = value && value.length ? value[1] : null
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getDashboard()
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.roomCode = null
      this.queryParams.roomName = null
      this.queryParams.pageNum = 1
      this.initDefaultDate()
      this.getDashboard()
      this.getList()
    },
    handleExport() {
      this.download('/space/statistics/export', {
        ...this.dashboardQuery()
      }, `房间预约数据统计_${new Date().getTime()}.xlsx`)
    },
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
    formatRate(value) {
      return `${Number(value || 0).toFixed(2)}%`
    }
  }
}
</script>

<style lang="scss" scoped>
.statistics-page {
  min-height: calc(100vh - 84px);
  padding: 16px;
  background: #f5f7fb;
}

.filter-bar {
  padding: 16px 16px 4px;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e6ebf2;
  border-radius: 4px;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  display: flex;
  align-items: center;
  min-height: 92px;
  padding: 16px;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e6ebf2;
  border-radius: 4px;
}

.summary-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 44px;
  width: 44px;
  height: 44px;
  margin-right: 12px;
  color: #ffffff;
  border-radius: 4px;
  font-size: 22px;
}

.summary-card__icon.primary {
  background: #2f80ed;
}

.summary-card__icon.success {
  background: #27ae60;
}

.summary-card__icon.teal {
  background: #00a6a6;
}

.summary-card__icon.danger {
  background: #eb5757;
}

.summary-card__icon.warning {
  background: #f2c94c;
}

.summary-card__icon.info {
  background: #6b7280;
}

.summary-card__content {
  min-width: 0;
}

.summary-card__label {
  color: #6b7280;
  font-size: 13px;
  line-height: 20px;
}

.summary-card__value {
  margin-top: 6px;
  color: #1f2937;
  font-size: 24px;
  font-weight: 600;
  line-height: 30px;
}

.panel-card {
  margin-bottom: 16px;
  border-radius: 4px;
}

.panel-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #1f2937;
  font-weight: 600;
}

.chart {
  width: 100%;
  height: 340px;
}

.table-card {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .statistics-page {
    padding: 12px;
  }

  .chart {
    height: 280px;
  }

  .summary-card__value {
    font-size: 20px;
  }
}
</style>
