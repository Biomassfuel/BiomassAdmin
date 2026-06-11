<template>
  <div class="app-container">

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="78px">
      <el-form-item label="预约编号" prop="reservationNo">
        <el-input v-model="queryParams.reservationNo" placeholder="请输入预约编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预约主题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入预约主题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预约类型" prop="reservationType">
        <el-select v-model="queryParams.reservationType" placeholder="全部" clearable>
          <el-option v-for="item in reservationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in reservationStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reservationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预约ID" prop="reservationId" align="center" width="90" />
      <el-table-column label="预约编号" prop="reservationNo" align="center" width="150" />
      <el-table-column label="预约主题" prop="title" align="center" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="类型" align="center" width="110">
        <template slot-scope="scope">{{ reservationTypeText(scope.row.reservationType) }}</template>
      </el-table-column>
      <el-table-column label="人数" prop="peopleCount" align="center" width="80" />
      <el-table-column label="状态" align="center" width="110">
        <template slot-scope="scope">{{ statusText(scope.row.status) }}</template>
      </el-table-column>
      <el-table-column label="提交时间" prop="submitTime" align="center" width="170" />
      <el-table-column label="审核人" prop="auditorName" align="center" width="110" />
      <el-table-column label="操作" align="center" width="170" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['space:reservation:query']">详情</el-button>
          <el-button v-if="scope.row.status === '1'" size="mini" type="text" icon="el-icon-close" @click="handleCancel(scope.row)" v-hasPermi="['space:reservation:cancel']">取消</el-button>
          <el-button v-if="scope.row.status === '2' || scope.row.status === '3'" size="mini" type="text" icon="el-icon-close" @click="handleCancelApproved(scope.row)" v-hasPermi="['space:reservation:cancel']">取消已通过</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

  </div>
</template>

<script>
import { listMyReservation, cancelReservation } from '@/api/space/reservation'
import { statusText, reservationTypeText, reservationTypeOptions, reservationStatusOptions } from './utils'

export default {
  name: 'SpaceMyReservation',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      reservationList: [],
      reservationTypeOptions,
      reservationStatusOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reservationNo: null,
        title: null,
        reservationType: null,
        status: null
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
    statusText,
    reservationTypeText,
    getList() {
      this.loading = true
      listMyReservation(this.queryParams).then(response => {
        this.reservationList = response.rows
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
      this.ids = selection.map(item => item.reservationId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      this.$router.push({
        path: '/space-reservation/detail',
        query: { reservationId: row.reservationId }
      })
    },
    handleCancel(row) {
      this.$modal.confirm('是否确认取消预约编号为"' + row.reservationNo + '"的申请？').then(function() {
        return cancelReservation(row.reservationId)
      }).then(() => {
        this.$modal.msgSuccess('取消成功')
        this.getList()
      }).catch(() => {})
    },
    handleCancelApproved(row) {
      this.$modal.confirm('是否确认发起预约编号为"' + row.reservationNo + '"的取消审核？').then(function() {
        return cancelReservation(row.reservationId)
      }).then(() => {
        this.$modal.msgSuccess('已提交取消审核')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>
