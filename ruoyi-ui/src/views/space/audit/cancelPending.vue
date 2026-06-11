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
      <el-form-item label="申请人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="请输入申请人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['space:reservation:export']">导出</el-button>
      </el-col>
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
      <el-table-column label="申请人" prop="applicantName" align="center" width="110" />
      <el-table-column label="人数" prop="peopleCount" align="center" width="80" />
      <el-table-column label="状态" align="center" width="110">
        <template slot-scope="scope">{{ statusText(scope.row.status) }}</template>
      </el-table-column>
      <el-table-column label="提交时间" prop="submitTime" align="center" width="170" />
      <el-table-column label="操作" align="center" width="220" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['space:reservation:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-check" @click="handleApprove(scope.row)" v-hasPermi="['space:cancelAudit:approve']">同意取消</el-button>
          <el-button size="mini" type="text" icon="el-icon-close" @click="handleReject(scope.row)" v-hasPermi="['space:cancelAudit:reject']">驳回取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <detail-dialog
      :visible.sync="detailOpen"
      :detail="detail"
      audit-item
      cancel-audit
      @approve-item="handleApproveItem"
      @reject-item="handleRejectItem"
    />

    <el-dialog :title="auditTitle" :visible.sync="auditOpen" width="520px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="88px">
        <el-form-item label="预约编号">
          <el-input :value="auditRow ? auditRow.reservationNo : ''" disabled />
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input v-model="auditForm.auditOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="auditOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCancelPendingReservation, getReservation, approveCancelReservation, rejectCancelReservation, approveCancelReservationItem, rejectCancelReservationItem } from '@/api/space/reservation'
import DetailDialog from '@/views/space/reservation/DetailDialog'
import { statusText, reservationTypeText, reservationTypeOptions } from '@/views/space/reservation/utils'

export default {
  name: 'SpaceCancelAuditPending',
  components: { DetailDialog },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      reservationList: [],
      detailOpen: false,
      detail: null,
      auditOpen: false,
      auditTitle: '',
      auditAction: '',
      auditRow: null,
      auditForm: {
        auditOpinion: ''
      },
      auditRules: {
        auditOpinion: [{ required: true, message: '审核意见不能为空', trigger: 'blur' }]
      },
      reservationTypeOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reservationNo: null,
        title: null,
        reservationType: null,
        applicantName: null
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
      listCancelPendingReservation(this.queryParams).then(response => {
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
      getReservation(row.reservationId).then(response => {
        this.detail = response.data
        this.detailOpen = true
      })
    },
    handleApprove(row) {
      this.openAuditDialog(row, 'approve')
    },
    handleReject(row) {
      this.openAuditDialog(row, 'reject')
    },
    openAuditDialog(row, action) {
      this.auditRow = row
      this.auditAction = action
      this.auditTitle = action === 'approve' ? '同意取消预约' : '驳回取消预约'
      this.auditForm.auditOpinion = action === 'approve' ? '同意取消' : ''
      this.auditOpen = true
      this.$nextTick(() => {
        if (this.$refs.auditForm) this.$refs.auditForm.clearValidate()
      })
    },
    submitAudit() {
      this.$refs.auditForm.validate(valid => {
        if (!valid || !this.auditRow) return
        const request = this.auditAction === 'approve' ? approveCancelReservation : rejectCancelReservation
        request(this.auditRow.reservationId, { auditOpinion: this.auditForm.auditOpinion }).then(() => {
          this.$modal.msgSuccess(this.auditAction === 'approve' ? '已同意取消' : '已驳回取消')
          this.auditOpen = false
          this.getList()
        })
      })
    },
    handleApproveItem(row) {
      approveCancelReservationItem(row.itemId, { auditOpinion: '同意取消场次' }).then(() => {
        this.$modal.msgSuccess('场次已取消')
        this.handleView(this.detail)
        this.getList()
      })
    },
    handleRejectItem(row) {
      this.$prompt('请输入驳回取消原因', '驳回取消场次', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '驳回取消原因不能为空'
      }).then(({ value }) => {
        return rejectCancelReservationItem(row.itemId, { auditOpinion: value })
      }).then(() => {
        this.$modal.msgSuccess('已驳回取消场次')
        this.handleView(this.detail)
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('space/reservation/export', {
        ...this.queryParams,
        cancelPendingOnly: true
      }, `cancel_pending_reservation_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
