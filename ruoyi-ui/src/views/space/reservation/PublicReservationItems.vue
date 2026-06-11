<template>
  <div class="public-reservation-items">
    <el-form
      v-if="showSearch"
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      label-width="78px"
    >
      <el-form-item label="预约编号" prop="reservationNo">
        <el-input v-model="queryParams.reservationNo" placeholder="请输入预约编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item v-if="!roomId" label="房间编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房间编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item v-if="!roomId" label="房间名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房间名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预约主题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入预约主题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预约类型" prop="reservationType">
        <el-select v-model="queryParams.reservationType" placeholder="全部" clearable>
          <el-option v-for="item in reservationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="预约人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="请输入预约人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item v-if="!fixedItemStatus" label="状态" prop="itemStatus">
        <el-select v-model="queryParams.itemStatus" placeholder="全部" clearable>
          <el-option v-for="item in publicItemStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row v-if="toolbar" :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="innerShowSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="itemList">
      <el-table-column label="预约编号" prop="reservationNo" align="center" width="150" />
      <el-table-column label="房间" align="center" min-width="160" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ roomText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约主题" prop="title" align="center" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="预约类型" align="center" width="110">
        <template slot-scope="scope">{{ reservationTypeText(scope.row.reservationType) }}</template>
      </el-table-column>
      <el-table-column label="预约人" prop="applicantName" align="center" width="110" />
      <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
      <el-table-column label="时间段" align="center" width="150">
        <template slot-scope="scope">{{ timeText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="人数" prop="peopleCount" align="center" width="80" />
      <el-table-column label="状态" align="center" width="120">
        <template slot-scope="scope">{{ publicStatusText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="预约备注" prop="detailRemark" align="center" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="90" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <public-detail-dialog
      :visible.sync="detailOpen"
      :reservation-id="detailReservationId"
      :initial-item="detailItem"
      :fixed-item-status="fixedItemStatus"
    />
  </div>
</template>

<script>
import { listPublicReservationItem } from '@/api/space/reservation-item'
import PublicDetailDialog from './PublicDetailDialog'
import { reservationTypeText, reservationTypeOptions } from './utils'

export default {
  name: 'PublicReservationItems',
  components: { PublicDetailDialog },
  props: {
    roomId: { type: [String, Number], default: null },
    toolbar: { type: Boolean, default: true },
    autoLoad: { type: Boolean, default: true },
    fixedItemStatus: { type: String, default: null }
  },
  data() {
    return {
      loading: false,
      total: 0,
      itemList: [],
      dateRange: [],
      innerShowSearch: true,
      detailOpen: false,
      detailReservationId: null,
      detailItem: null,
      reservationTypeOptions,
      publicItemStatusOptions: [
        { label: '已通过', value: '2' },
        { label: '取消待审占用', value: '1' },
        { label: '已结束', value: '6' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reservationNo: null,
        roomCode: null,
        roomName: null,
        title: null,
        reservationType: null,
        applicantName: null,
        itemStatus: null,
        bookingDateStart: null,
        bookingDateEnd: null
      }
    }
  },
  computed: {
    showSearch() {
      return !this.toolbar || this.innerShowSearch
    }
  },
  watch: {
    roomId() {
      this.queryParams.pageNum = 1
      if (this.autoLoad) this.getList()
    }
  },
  created() {
    if (this.autoLoad) this.getList()
  },
  activated() {
    if (this.autoLoad) this.getList()
  },
  methods: {
    reservationTypeText,
    getList() {
      this.loading = true
      listPublicReservationItem({
        ...this.queryParams,
        roomId: this.roomId || null,
        itemStatus: this.fixedItemStatus || this.queryParams.itemStatus,
        bookingDateStart: this.dateRange && this.dateRange.length ? this.dateRange[0] : null,
        bookingDateEnd: this.dateRange && this.dateRange.length ? this.dateRange[1] : null
      }).then(response => {
        this.itemList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.itemStatus = null
      this.handleQuery()
    },
    handleView(row) {
      this.detailItem = row
      this.detailReservationId = row.reservationId
      this.detailOpen = true
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
      if (row.itemStatus === '6' || row.reservationStatus === '6') return '已结束'
      if (row.itemStatus === '1' && row.auditType === '1') return '取消待审占用'
      if (row.itemStatus === '2') return '已通过'
      return '-'
    }
  }
}
</script>
