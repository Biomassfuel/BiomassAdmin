<template>
  <el-dialog title="预约详情" :visible.sync="open" width="960px" append-to-body>
    <el-empty v-if="!summary && !loading" description="请选择预约记录查看详情" />

    <div v-else v-loading="loading">
      <el-descriptions v-if="summary" :column="3" border>
        <el-descriptions-item label="预约编号">{{ summary.reservationNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约类型">{{ reservationTypeText(summary.reservationType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ publicStatusText(summary) }}</el-descriptions-item>
        <el-descriptions-item label="预约主题">{{ summary.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约人">{{ summary.applicantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约人数">{{ summary.peopleCount == null ? '-' : summary.peopleCount }}</el-descriptions-item>
        <el-descriptions-item label="预约备注" :span="3">{{ summary.detailRemark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-table v-if="summary" class="mt16" :data="pagedItems">
        <el-table-column label="房间" align="center" min-width="160" :show-overflow-tooltip="true">
          <template slot-scope="scope">{{ roomText(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
        <el-table-column label="星期" align="center" width="90">
          <template slot-scope="scope">{{ weekdayText(scope.row.weekday) }}</template>
        </el-table-column>
        <el-table-column label="时间段" align="center" width="150">
          <template slot-scope="scope">{{ timeText(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="120">
          <template slot-scope="scope">{{ publicStatusText(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="预约备注" prop="detailRemark" align="center" :show-overflow-tooltip="true" />
      </el-table>

      <pagination
        v-if="summary"
        v-show="items.length > 0"
        :total="items.length"
        :page.sync="pager.pageNum"
        :limit.sync="pager.pageSize"
      />
    </div>
  </el-dialog>
</template>

<script>
import { listPublicReservationItem } from '@/api/space/reservation-item'
import { reservationTypeText, weekdayText } from './utils'

export default {
  name: 'PublicReservationDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    reservationId: { type: [String, Number], default: null },
    initialItem: { type: Object, default: null },
    fixedItemStatus: { type: String, default: null }
  },
  data() {
    return {
      loading: false,
      items: [],
      pager: { pageNum: 1, pageSize: 10 }
    }
  },
  computed: {
    open: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    },
    summary() {
      return this.initialItem || this.items[0] || null
    },
    pagedItems() {
      const start = (this.pager.pageNum - 1) * this.pager.pageSize
      return this.items.slice(start, start + this.pager.pageSize)
    }
  },
  watch: {
    visible(value) {
      if (value) this.getList()
    },
    reservationId() {
      if (this.visible) this.getList()
    }
  },
  methods: {
    reservationTypeText,
    weekdayText,
    getList() {
      if (!this.reservationId) {
        this.items = this.initialItem ? [this.initialItem] : []
        return
      }
      this.loading = true
      listPublicReservationItem({
        pageNum: 1,
        pageSize: 500,
        reservationId: this.reservationId,
        itemStatus: this.fixedItemStatus || null
      }).then(response => {
        this.items = response.rows || []
        this.pager.pageNum = 1
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    roomText(row) {
      return [row.roomCode, row.roomName].filter(Boolean).join(' ') || '-'
    },
    timeText(row) {
      return `${this.formatTime(row.startTime)} - ${this.formatTime(row.endTime)}`
    },
    formatTime(value) {
      return String(value || '').slice(0, 5)
    },
    publicStatusText(row) {
      if (!row) return '-'
      if (row.status) return this.reservationStatusText(row.status)
      if (row.itemStatus === '6' || row.reservationStatus === '6') return '已结束'
      if (row.itemStatus === '1' && row.auditType === '1') return '取消待审占用'
      if (row.itemStatus === '2') return '已通过'
      return '-'
    },
    reservationStatusText(status) {
      return {
        '0': '草稿',
        '1': '待审核',
        '2': '已通过',
        '3': '部分通过',
        '4': '已驳回',
        '5': '已取消',
        '6': '已结束'
      }[status] || status || '-'
    }
  }
}
</script>

<style scoped>
.mt16 {
  margin-top: 16px;
}
</style>
