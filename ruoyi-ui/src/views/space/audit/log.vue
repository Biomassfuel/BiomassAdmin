<template>
  <div class="app-container">

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="78px">
      <el-form-item label="预约ID" prop="reservationId">
        <el-input v-model="queryParams.reservationId" placeholder="请输入预约ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="场次ID" prop="itemId">
        <el-input v-model="queryParams.itemId" placeholder="请输入场次ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核动作" prop="auditAction">
        <el-select v-model="queryParams.auditAction" placeholder="全部" clearable>
          <el-option v-for="item in auditActionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人" prop="auditorName">
        <el-input v-model="queryParams.auditorName" placeholder="请输入操作人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['space:auditLog:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志ID" prop="logId" align="center" width="90" />
      <el-table-column label="预约ID" prop="reservationId" align="center" width="100" />
      <el-table-column label="场次ID" prop="itemId" align="center" width="100" />
      <el-table-column label="动作" align="center" width="110">
        <template slot-scope="scope">{{ auditActionText(scope.row.auditAction) }}</template>
      </el-table-column>
      <el-table-column label="前状态" align="center" width="110">
        <template slot-scope="scope">{{ statusText(scope.row.beforeStatus) }}</template>
      </el-table-column>
      <el-table-column label="后状态" align="center" width="110">
        <template slot-scope="scope">{{ statusText(scope.row.afterStatus) }}</template>
      </el-table-column>
      <el-table-column label="操作人" prop="auditorName" align="center" width="120" />
      <el-table-column label="意见" prop="auditOpinion" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="时间" prop="auditTime" align="center" width="170" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listAuditLog } from '@/api/space/audit-log'
import { auditActionText, auditActionOptions, statusText } from '@/views/space/reservation/utils'

export default {
  name: 'SpaceAuditLog',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      logList: [],
      auditActionOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reservationId: null,
        itemId: null,
        auditAction: null,
        auditorName: null
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
    auditActionText,
    statusText,
    getList() {
      this.loading = true
      listAuditLog(this.queryParams).then(response => {
        this.logList = response.rows
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
      this.ids = selection.map(item => item.logId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleExport() {
      this.download('space/audit-log/export', {
        ...this.queryParams
      }, `audit_log_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
