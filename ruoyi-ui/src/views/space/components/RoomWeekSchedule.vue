<template>
  <el-card shadow="never" class="schedule-card mt16">
    <div slot="header" class="schedule-card__header">
      <div>
        <span class="schedule-card__title">{{ title }}</span>
        <span class="schedule-card__range">{{ weekRangeText }}</span>
      </div>
      <div class="schedule-card__actions">
        <el-button size="mini" icon="el-icon-arrow-left" :disabled="weekPage <= 1" @click="changeWeek(weekPage - 1)">上一周</el-button>
        <el-pagination
          small
          layout="pager"
          :current-page.sync="weekPage"
          :page-size="1"
          :total="maxWeeks"
          @current-change="handleWeekChange"
        />
        <el-button size="mini" :disabled="weekPage >= maxWeeks" @click="changeWeek(weekPage + 1)">
          下一周<i class="el-icon-arrow-right el-icon--right" />
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="schedule-table-wrap">
      <table class="schedule-table">
        <thead>
          <tr>
            <th class="schedule-time-head">时间段</th>
            <th v-for="day in weekDays" :key="day.date" class="schedule-day-head">
              <div>{{ day.weekday }}</div>
              <span>{{ day.date }}</span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="period in periods" :key="period.periodId || period.periodName">
            <td class="schedule-time-cell">
              <strong>{{ period.periodName }}</strong>
              <span>{{ formatTime(period.startTime) }}-{{ formatTime(period.endTime) }}</span>
            </td>
            <td v-for="day in weekDays" :key="`${period.periodId}-${day.date}`" class="schedule-day-cell">
              <el-tooltip
                v-for="item in cellItems(day.date, period)"
                :key="item.itemId"
                effect="dark"
                placement="top"
                :open-delay="300"
              >
                <div slot="content" class="schedule-tooltip">
                  <div>预约编号：{{ item.reservationNo || '-' }}</div>
                  <div>申请人：{{ item.applicantName || '-' }}</div>
                  <div>时间：{{ formatTime(item.startTime) }}-{{ formatTime(item.endTime) }}</div>
                  <div>状态：{{ occupancyStatusText(item) }}</div>
                </div>
                <div class="schedule-block" :class="blockClass(item)">
                  <div class="schedule-block__status">{{ occupancyStatusText(item) }}</div>
                  <div class="schedule-block__org">{{ item.orgName || '未填单位' }}</div>
                  <div class="schedule-block__title">{{ item.title || '未填主题' }}</div>
                  <div class="schedule-block__meta">
                    <span>{{ formatTime(item.startTime) }}-{{ formatTime(item.endTime) }}</span>
                    <span>{{ peopleText(item) }}</span>
                  </div>
                </div>
              </el-tooltip>
            </td>
          </tr>
          <tr v-if="periods.length === 0">
            <td class="schedule-empty" colspan="8">暂无启用时间段</td>
          </tr>
        </tbody>
      </table>
    </div>
  </el-card>
</template>

<script>
import { listReservationItem } from '@/api/space/reservation-item'
import { listTimePeriod } from '@/api/space/time-period'
import { fetchAllPages } from '@/utils/paged-list'
import { formatDate, standardPeriods } from '@/views/space/reservation/utils'

export default {
  name: 'RoomWeekSchedule',
  props: {
    roomId: {
      type: [String, Number],
      default: null
    },
    title: {
      type: String,
      default: '房间占用课表'
    },
    maxWeeks: {
      type: Number,
      default: 12
    }
  },
  data() {
    return {
      loading: false,
      itemList: [],
      periods: [],
      weekPage: 1
    }
  },
  computed: {
    weekStartDate() {
      const start = this.startOfWeek(new Date())
      start.setDate(start.getDate() + (this.weekPage - 1) * 7)
      return start
    },
    weekDays() {
      const names = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日']
      return names.map((weekday, index) => {
        const date = new Date(this.weekStartDate)
        date.setDate(date.getDate() + index)
        return {
          weekday,
          date: formatDate(date)
        }
      })
    },
    weekRangeText() {
      if (this.weekDays.length === 0) return ''
      return `${this.weekDays[0].date} 至 ${this.weekDays[this.weekDays.length - 1].date}`
    }
  },
  watch: {
    roomId: {
      immediate: true,
      handler() {
        this.weekPage = 1
        this.getList()
      }
    }
  },
  created() {
    this.getPeriods()
  },
  activated() {
    this.getPeriods()
    this.getList()
  },
  methods: {
    getPeriods() {
      fetchAllPages(listTimePeriod, { status: '0' }).then(rows => {
        this.periods = standardPeriods(rows).sort((a, b) => {
          const orderA = a.orderNum == null ? 0 : Number(a.orderNum)
          const orderB = b.orderNum == null ? 0 : Number(b.orderNum)
          if (orderA !== orderB) return orderA - orderB
          return this.timeToMinutes(a.startTime) - this.timeToMinutes(b.startTime)
        })
      })
    },
    getList() {
      if (!this.roomId) {
        this.itemList = []
        this.loading = false
        return
      }
      this.loading = true
      listReservationItem({
        pageNum: 1,
        pageSize: 500,
        roomId: this.roomId,
        bookingDateStart: this.weekDays[0].date,
        bookingDateEnd: this.weekDays[this.weekDays.length - 1].date,
        occupiedOnly: true
      }).then(response => {
        this.itemList = response.rows || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    timeToMinutes(value) {
      const parts = String(value || '0:0').split(':')
      return Number(parts[0] || 0) * 60 + Number(parts[1] || 0)
    },
    startOfWeek(date) {
      const result = new Date(date.getFullYear(), date.getMonth(), date.getDate())
      const day = result.getDay() || 7
      result.setDate(result.getDate() - day + 1)
      return result
    },
    formatTime(value) {
      return String(value || '').slice(0, 5)
    },
    cellItems(date, period) {
      const periodStart = this.timeToMinutes(period.startTime)
      const periodEnd = this.timeToMinutes(period.endTime)
      return (this.itemList || []).filter(item => {
        if (item.bookingDate !== date) return false
        return this.timeToMinutes(item.startTime) < periodEnd && this.timeToMinutes(item.endTime) > periodStart
      }).sort((a, b) => this.timeToMinutes(a.startTime) - this.timeToMinutes(b.startTime))
    },
    peopleText(item) {
      return item.peopleCount != null ? `${item.peopleCount}人` : '人数未填'
    },
    occupancyStatusText(item) {
      if (item.itemStatus === '2') return '已通过'
      if (item.itemStatus === '1' && item.auditType === '1') return '取消待审占用'
      if (item.itemStatus === '1') return '待审核占用'
      return '-'
    },
    blockClass(item) {
      if (item.itemStatus === '2') return 'is-approved'
      if (item.auditType === '1') return 'is-cancel-pending'
      return 'is-pending'
    },
    handleWeekChange(page) {
      this.weekPage = page
      this.getList()
    },
    changeWeek(page) {
      if (page < 1 || page > this.maxWeeks || page === this.weekPage) return
      this.weekPage = page
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.mt16 {
  margin-top: 16px;
}

.schedule-card {
  border-radius: 4px;
}

.schedule-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.schedule-card__title {
  color: #1f2f3d;
  font-size: 16px;
  font-weight: 600;
}

.schedule-card__range {
  margin-left: 12px;
  color: #606266;
  font-size: 13px;
}

.schedule-card__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.schedule-table-wrap {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  min-width: 1080px;
  table-layout: fixed;
  border-collapse: collapse;
  border: 1px solid #dcdfe6;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #dcdfe6;
}

.schedule-time-head {
  width: 130px;
  background: #eef3fb;
}

.schedule-day-head {
  height: 50px;
  color: #1f2f3d;
  background: #dbe5f5;
  text-align: center;
}

.schedule-day-head div {
  font-weight: 600;
}

.schedule-day-head span {
  display: block;
  margin-top: 3px;
  color: #606266;
  font-size: 12px;
  font-weight: 400;
}

.schedule-time-cell {
  height: 132px;
  color: #1f2f3d;
  background: #dbe5f5;
  text-align: center;
}

.schedule-time-cell strong,
.schedule-time-cell span {
  display: block;
}

.schedule-time-cell strong {
  margin-bottom: 8px;
  font-size: 14px;
}

.schedule-time-cell span {
  font-size: 12px;
}

.schedule-day-cell {
  height: 132px;
  padding: 8px;
  vertical-align: top;
  background: #fbfdff;
}

.schedule-block {
  position: relative;
  display: flex;
  min-height: 82px;
  margin-bottom: 8px;
  padding: 10px 12px 9px;
  overflow: hidden;
  flex-direction: column;
  justify-content: center;
  border-radius: 6px;
  color: #fff;
  font-size: 13px;
  line-height: 1.4;
  cursor: default;
  box-shadow: 0 6px 14px rgba(11, 74, 128, 0.16);
  transition: transform 0.16s ease, box-shadow 0.16s ease;
}

.schedule-block::before {
  position: absolute;
  top: 8px;
  bottom: 8px;
  left: 0;
  width: 4px;
  border-radius: 0 4px 4px 0;
  background: rgba(255, 255, 255, 0.72);
  content: '';
}

.schedule-block:hover {
  box-shadow: 0 8px 18px rgba(11, 74, 128, 0.22);
  transform: translateY(-1px);
}

.schedule-block:last-child {
  margin-bottom: 0;
}

.schedule-block.is-approved {
  background: linear-gradient(135deg, #116fcb 0%, #0f80d8 100%);
}

.schedule-block.is-pending {
  background: linear-gradient(135deg, #d88922 0%, #eba83b 100%);
  box-shadow: 0 6px 14px rgba(186, 112, 12, 0.16);
}

.schedule-block.is-cancel-pending {
  background: linear-gradient(135deg, #c46d1c 0%, #df8a2d 100%);
  box-shadow: 0 6px 14px rgba(168, 84, 11, 0.16);
}

.schedule-block__status {
  position: absolute;
  top: 7px;
  right: 8px;
  max-width: 82px;
  padding: 1px 6px;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  font-size: 11px;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.schedule-block__org,
.schedule-block__title,
.schedule-block__meta {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.schedule-block__org {
  padding-right: 72px;
  font-weight: 600;
}

.schedule-block__title {
  margin-top: 3px;
}

.schedule-block__meta {
  display: flex;
  margin-top: 7px;
  justify-content: space-between;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
}

.schedule-tooltip {
  line-height: 1.8;
}

.schedule-empty {
  height: 120px;
  color: #909399;
  text-align: center;
}

::v-deep .schedule-card__actions .el-pagination {
  padding: 0;
}
</style>
