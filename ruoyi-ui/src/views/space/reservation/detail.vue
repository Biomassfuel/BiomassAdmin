<template>
  <div class="app-container">

    <el-empty v-if="!detail" description="请选择预约记录查看详情" />

    <div v-else>
      <el-descriptions :column="3" border>
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

      <el-table class="mt16" :data="pagedItems">
        <el-table-column label="场次ID" prop="itemId" align="center" width="90" />
        <el-table-column label="房间" align="center" min-width="150">
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
      </el-table>

      <pagination
        v-show="items.length > 0"
        :total="items.length"
        :page.sync="itemPager.pageNum"
        :limit.sync="itemPager.pageSize"
        @pagination="handleItemPagination"
      />
    </div>
  </div>
</template>

<script>
import { getReservation } from '@/api/space/reservation'
import { statusText, itemStatusText, reservationTypeText, weekdayText } from './utils'

export default {
  name: 'SpaceReservationDetail',
  data() {
    return {
      detail: null,
      itemPager: { pageNum: 1, pageSize: 10 },
      queryParams: {
        reservationId: null
      }
    }
  },
  computed: {
    items() {
      return (this.detail && this.detail.items) || []
    },
    pagedItems() {
      const start = (this.itemPager.pageNum - 1) * this.itemPager.pageSize
      return this.items.slice(start, start + this.itemPager.pageSize)
    }
  },
  created() {
    this.syncRouteReservationId()
  },
  activated() {
    this.syncRouteReservationId()
  },
  watch: {
    '$route.query.reservationId'() {
      this.syncRouteReservationId()
    }
  },
  methods: {
    statusText,
    itemStatusText,
    reservationTypeText,
    weekdayText,
    syncRouteReservationId() {
      const routeReservationId = this.$route && this.$route.query ? this.$route.query.reservationId : null
      const reservationId = routeReservationId || this.queryParams.reservationId
      if (!reservationId) {
        return
      }
      if (routeReservationId) {
        this.queryParams.reservationId = routeReservationId
      }
      this.handleQuery()
    },
    handleItemPagination() {},
    handleQuery() {
      if (!this.queryParams.reservationId) {
        this.$modal.msgWarning('请输入预约ID')
        return
      }
      getReservation(this.queryParams.reservationId).then(response => {
        this.detail = response.data
        this.itemPager.pageNum = 1
      })
    },
    resetQuery() {
      this.detail = null
      this.itemPager.pageNum = 1
    }
  }
}
</script>

<style lang="scss" scoped>
.mt16 {
  margin-top: 16px;
}
</style>
