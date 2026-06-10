<template>
  <div class="app-container">

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="78px">
      <el-form-item label="预约日期" prop="bookingDate">
        <el-date-picker
          v-model="queryParams.bookingDate"
          value-format="yyyy-MM-dd"
          type="date"
          placeholder="请选择预约日期"
          clearable
        />
      </el-form-item>
      <el-form-item label="房间编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房间编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房间名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="itemStatus">
        <el-select v-model="queryParams.itemStatus" placeholder="全部" clearable>
          <el-option v-for="item in itemStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['space:reservationItem:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
      <el-table-column label="星期" align="center" width="90">
        <template slot-scope="scope">{{ weekdayText(scope.row.weekday) }}</template>
      </el-table-column>
      <el-table-column label="房间编号" prop="roomCode" align="center" width="120" />
      <el-table-column label="房间名称" prop="roomName" align="center" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="时间段" align="center" width="150">
        <template slot-scope="scope">{{ scope.row.startTime }} - {{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="120">
        <template slot-scope="scope">{{ itemStatusText(scope.row.itemStatus) }}</template>
      </el-table-column>
      <el-table-column label="冲突" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.conflictFlag === '1' ? 'danger' : 'success'">{{ scope.row.conflictFlag === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="冲突原因" prop="conflictReason" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="90" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.reservationId"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['space:reservation:query']"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <detail-dialog :visible.sync="detailOpen" :detail="detail" />
  </div>
</template>

<script>
import { listReservationItem } from '@/api/space/reservation-item'
import { getReservation } from '@/api/space/reservation'
import DetailDialog from './DetailDialog'
import { itemStatusText, itemStatusOptions, weekdayText } from './utils'

export default {
  name: 'SpaceOccupancy',
  components: { DetailDialog },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      itemList: [],
      detailOpen: false,
      detail: null,
      itemStatusOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bookingDate: null,
        roomCode: null,
        roomName: null,
        itemStatus: null
      }
    }
  },
  created() {
    this.getList()
  },
  activated() {
    this.getList()
  },
  methods: {
    itemStatusText,
    weekdayText,
    getList() {
      this.loading = true
      listReservationItem(this.queryParams).then(response => {
        this.itemList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.itemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      getReservation(row.reservationId).then(response => {
        this.detail = response.data
        this.detailOpen = true
      })
    },
    handleExport() {
      this.download('space/reservation/item/export', {
        ...this.queryParams
      }, `reservation_item_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
