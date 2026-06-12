<template>
  <div class="approved-room-list">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="78px">
      <el-form-item label="房间编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房间编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房间名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="楼栋" prop="buildingName">
        <el-input v-model="queryParams.buildingName" placeholder="请输入楼栋" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间类型" prop="typeId">
        <el-select v-model="queryParams.typeId" placeholder="请选择房间类型" clearable filterable>
          <el-option v-for="item in typeOptions" :key="item.typeId" :label="item.typeName" :value="item.typeId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roomList">
      <el-table-column label="房间编号" prop="roomCode" align="center" width="120" />
      <el-table-column label="房间名称" prop="roomName" align="center" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="楼栋" prop="buildingName" align="center" width="110" />
      <el-table-column label="楼层" prop="floorNo" align="center" width="90" />
      <el-table-column label="房间类型" prop="roomType" align="center" width="120" />
      <el-table-column label="容量" align="center" min-width="130">
        <template slot-scope="scope">{{ capacityText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="可预约状态" align="center" width="110">
        <template slot-scope="scope">
          <el-tag :type="scope.row.bookable === '0' ? 'success' : 'info'">
            {{ scope.row.bookable === '0' ? '可预约' : '不可预约' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="房间状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="roomStatusTag(scope.row.status)">
            {{ roomStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-tickets" @click="handleRecords(scope.row)">预约记录</el-button>
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

    <el-dialog :title="recordTitle" :visible.sync="recordOpen" width="92vw" append-to-body>
      <public-reservation-summaries
        v-if="recordOpen"
        :room-id="recordRoomId"
        :toolbar="false"
      />
    </el-dialog>
  </div>
</template>

<script>
import { listApprovedReservationRoom } from '@/api/space/room'
import { listPublicRoomType } from '@/api/space/room-type'
import { fetchAllPages } from '@/utils/paged-list'
import PublicReservationSummaries from './PublicReservationSummaries'

export default {
  name: 'ApprovedRoomList',
  components: { PublicReservationSummaries },
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      roomList: [],
      typeOptions: [],
      recordOpen: false,
      recordRoomId: null,
      recordTitle: '已通过预约记录',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roomCode: null,
        roomName: null,
        buildingName: null,
        typeId: null,
        approvedReservationOnly: true
      }
    }
  },
  created() {
    this.getTypeOptions()
    this.getList()
  },
  activated() {
    this.getTypeOptions()
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listApprovedReservationRoom({
        ...this.queryParams,
        approvedReservationOnly: true
      }).then(response => {
        this.roomList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getTypeOptions() {
      fetchAllPages(listPublicRoomType, { status: '0' }).then(rows => {
        this.typeOptions = rows
      })
    },
    capacityText(row) {
      if (row.capacityDesc) return row.capacityDesc
      if (row.capacityMin != null && row.capacityMax != null) return `${row.capacityMin}-${row.capacityMax}人`
      if (row.capacityMax != null) return `${row.capacityMax}人以内`
      return '-'
    },
    roomStatusText(status) {
      if (status === '0') return '正常'
      if (status === '2') return '维护'
      if (status === '1') return '停用'
      return '-'
    },
    roomStatusTag(status) {
      if (status === '0') return 'success'
      if (status === '2') return 'warning'
      return 'danger'
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.approvedReservationOnly = true
      this.handleQuery()
    },
    handleRecords(row) {
      this.recordRoomId = row.roomId
      this.recordTitle = `${row.roomCode || ''} ${row.roomName || ''} 已通过预约记录`.trim()
      this.recordOpen = true
    }
  }
}
</script>
