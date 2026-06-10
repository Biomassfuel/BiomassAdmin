<template>
  <div class="app-container">
    <div class="detail-toolbar">
      <el-button plain icon="el-icon-back" size="mini" @click="handleBack">返回</el-button>
    </div>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="detail-card__header">
        <span>房间信息</span>
        <el-tag :type="realtimeStatus.occupied ? 'danger' : 'success'">
          {{ realtimeStatus.text }}
        </el-tag>
      </div>
      <el-descriptions v-if="room" :column="3" border>
        <el-descriptions-item label="房间编号">{{ room.roomCode }}</el-descriptions-item>
        <el-descriptions-item label="房间名称">{{ room.roomName }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ room.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ room.floorNo }}</el-descriptions-item>
        <el-descriptions-item label="房间类型">{{ room.roomType }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ room.area || '-' }}</el-descriptions-item>
        <el-descriptions-item label="容量">{{ capacityText(room) }}</el-descriptions-item>
        <el-descriptions-item label="归属单位">{{ room.assignedOrgName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="可预约">
          <el-tag :type="room.bookable === '0' ? 'success' : 'info'">
            {{ room.bookable === '0' ? '可预约' : '不可预约' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="room.status === '0' ? 'success' : 'danger'">
            {{ room.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备说明" :span="3">{{ room.equipmentDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="位置说明" :span="3">{{ room.locationDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ room.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="房间信息加载中" />
    </el-card>

    <el-form class="mt16" :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="78px">
      <el-form-item label="预约日期" prop="bookingDate">
        <el-date-picker
          v-model="queryParams.bookingDate"
          value-format="yyyy-MM-dd"
          type="date"
          placeholder="请选择预约日期"
          clearable
        />
      </el-form-item>
      <el-form-item label="场次状态" prop="itemStatus">
        <el-select v-model="queryParams.itemStatus" placeholder="全部" clearable>
          <el-option v-for="item in itemStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="预约编号" prop="reservationNo">
        <el-input v-model="queryParams.reservationNo" placeholder="请输入预约编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预约主题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入预约主题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="申请人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="请输入申请人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="itemList">
      <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
      <el-table-column label="星期" align="center" width="90">
        <template slot-scope="scope">{{ weekdayText(scope.row.weekday) }}</template>
      </el-table-column>
      <el-table-column label="时间段" align="center" width="150">
        <template slot-scope="scope">{{ scope.row.startTime }} - {{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="场次状态" align="center" width="120">
        <template slot-scope="scope">{{ itemStatusText(scope.row.itemStatus) }}</template>
      </el-table-column>
      <el-table-column label="预约编号" prop="reservationNo" align="center" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="预约主题" prop="title" align="center" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="申请人" prop="applicantName" align="center" width="110" />
      <el-table-column label="申请单位" prop="orgName" align="center" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="冲突" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.conflictFlag === '1' ? 'danger' : 'success'">
            {{ scope.row.conflictFlag === '1' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="冲突原因" prop="conflictReason" align="center" min-width="160" :show-overflow-tooltip="true" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getRoom } from '@/api/space/room'
import { listReservationItem } from '@/api/space/reservation-item'
import { formatDate, itemStatusOptions, itemStatusText, weekdayText } from './utils'

export default {
  name: 'SpaceOccupancyDetail',
  data() {
    return {
      loading: true,
      room: null,
      itemList: [],
      total: 0,
      hasActivated: false,
      realtimeStatus: {
        occupied: false,
        text: '空闲'
      },
      itemStatusOptions,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roomId: null,
        bookingDate: null,
        itemStatus: null,
        reservationNo: null,
        title: null,
        applicantName: null
      }
    }
  },
  watch: {
    '$route.params.roomId'(roomId, oldRoomId) {
      if (roomId && roomId !== oldRoomId) {
        this.queryParams.pageNum = 1
        this.loadPage()
      }
    }
  },
  created() {
    this.loadPage()
  },
  activated() {
    if (this.hasActivated) {
      this.loadPage()
    }
    this.hasActivated = true
  },
  methods: {
    itemStatusText,
    weekdayText,
    loadPage() {
      this.initRoomId()
      this.getRoomInfo()
      this.getRealtimeStatus()
      this.getList()
    },
    initRoomId() {
      this.queryParams.roomId = this.$route.params && this.$route.params.roomId
    },
    getRoomInfo() {
      if (!this.queryParams.roomId) return
      getRoom(this.queryParams.roomId).then(response => {
        this.room = response.data
      })
    },
    getList() {
      if (!this.queryParams.roomId) {
        this.itemList = []
        this.total = 0
        this.loading = false
        return
      }
      this.loading = true
      listReservationItem(this.queryParams).then(response => {
        this.itemList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getRealtimeStatus() {
      if (!this.queryParams.roomId) return
      const now = new Date()
      listReservationItem({
        pageNum: 1,
        pageSize: 500,
        roomId: this.queryParams.roomId,
        bookingDate: formatDate(now)
      }).then(response => {
        const currentMinutes = this.timeToMinutes(`${now.getHours()}:${now.getMinutes()}`)
        const activeItem = (response.rows || []).find(item => {
          if (!['1', '2'].includes(item.itemStatus)) return false
          const start = this.timeToMinutes(item.startTime)
          const end = this.timeToMinutes(item.endTime)
          return start <= currentMinutes && currentMinutes < end
        })
        this.realtimeStatus = activeItem
          ? { occupied: true, text: `占用中 ${activeItem.startTime} - ${activeItem.endTime}` }
          : { occupied: false, text: '空闲' }
      })
    },
    timeToMinutes(value) {
      const parts = String(value || '0:0').split(':')
      return Number(parts[0] || 0) * 60 + Number(parts[1] || 0)
    },
    capacityText(row) {
      if (!row) return '-'
      if (row.capacityDesc) return row.capacityDesc
      if (row.capacityMin != null && row.capacityMax != null) return `${row.capacityMin}-${row.capacityMax}人`
      if (row.capacityMax != null) return `${row.capacityMax}人以内`
      return '-'
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      const roomId = this.queryParams.roomId
      this.resetForm('queryForm')
      this.queryParams.roomId = roomId
      this.handleQuery()
    },
    handleBack() {
      this.$tab.closeOpenPage({ path: '/space-reservation/occupancy' })
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.detail-card {
  border-radius: 4px;
}

.detail-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mt16 {
  margin-top: 16px;
}
</style>
