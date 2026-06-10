<template>
  <el-dialog title="预约详情" :visible.sync="open" width="960px" append-to-body>
    <el-descriptions v-if="detail" :column="3" border>
      <el-descriptions-item label="预约编号">{{ detail.reservationNo }}</el-descriptions-item>
      <el-descriptions-item label="预约类型">{{ reservationTypeText(detail.reservationType) }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
      <el-descriptions-item label="预约主题">{{ detail.title }}</el-descriptions-item>
      <el-descriptions-item label="申请人">{{ detail.applicantName }}</el-descriptions-item>
      <el-descriptions-item label="预约人数">{{ detail.peopleCount }}</el-descriptions-item>
      <el-descriptions-item label="申请单位">{{ detail.orgName }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ detail.applicantPhone }}</el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detail.submitTime }}</el-descriptions-item>
      <el-descriptions-item label="预约用途" :span="3">{{ detail.purpose || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="3">{{ detail.detailRemark || '-' }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="3">{{ detail.rejectReason || '-' }}</el-descriptions-item>
    </el-descriptions>

    <el-table v-if="detail" class="mt16" :data="pagedItems">
      <el-table-column label="场次ID" prop="itemId" align="center" width="90" />
      <el-table-column label="房间" align="center" min-width="140">
        <template slot-scope="scope">{{ scope.row.roomCode }} {{ scope.row.roomName }}</template>
      </el-table-column>
      <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
      <el-table-column label="星期" align="center" width="90">
        <template slot-scope="scope">{{ weekdayText(scope.row.weekday) }}</template>
      </el-table-column>
      <el-table-column label="时间" align="center" width="150">
        <template slot-scope="scope">{{ scope.row.startTime }} - {{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="120">
        <template slot-scope="scope">{{ itemStatusText(scope.row.itemStatus) }}</template>
      </el-table-column>
      <el-table-column label="驳回原因" prop="rejectReason" align="center" :show-overflow-tooltip="true" />
      <el-table-column v-if="auditItem && detail.reservationType === '1'" label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="canAuditItem(scope.row)" size="mini" type="text" icon="el-icon-check" @click="$emit('approve-item', scope.row)">通过</el-button>
          <el-button v-if="canAuditItem(scope.row)" size="mini" type="text" icon="el-icon-close" @click="$emit('reject-item', scope.row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="detail"
      v-show="items.length > 0"
      :total="items.length"
      :page.sync="pager.pageNum"
      :limit.sync="pager.pageSize"
    />
  </el-dialog>
</template>

<script>
import { statusText, itemStatusText, reservationTypeText, weekdayText } from './utils'

export default {
  name: 'SpaceReservationDetailDialog',
  props: {
    visible: { type: Boolean, default: false },
    detail: { type: Object, default: null },
    auditItem: { type: Boolean, default: false }
  },
  data() {
    return {
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
    items() {
      return (this.detail && this.detail.items) || []
    },
    pagedItems() {
      const start = (this.pager.pageNum - 1) * this.pager.pageSize
      return this.items.slice(start, start + this.pager.pageSize)
    }
  },
  watch: {
    detail() {
      this.pager.pageNum = 1
    }
  },
  methods: {
    statusText,
    itemStatusText,
    reservationTypeText,
    weekdayText,
    canAuditItem(row) {
      return row && (row.itemStatus === '1' || row.itemStatus === '4')
    }
  }
}
</script>

<style scoped>
.mt16 {
  margin-top: 16px;
}
</style>
