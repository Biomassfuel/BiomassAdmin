<template>
  <el-card shadow="never" class="room-info-card">
    <div slot="header" class="room-info-card__header">
      <span>{{ title }}</span>
      <el-tag v-if="realtimeStatus" :type="realtimeStatus.occupied ? 'danger' : 'success'">
        {{ realtimeStatus.text }}
      </el-tag>
    </div>
    <el-descriptions v-if="room" :column="column" border>
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
      <el-descriptions-item label="设备说明" :span="descriptionSpan">{{ room.equipmentDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="位置说明" :span="descriptionSpan">{{ room.locationDesc || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="descriptionSpan">{{ room.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-empty v-else description="房间信息加载中" />
  </el-card>
</template>

<script>
export default {
  name: 'RoomInfoCard',
  props: {
    room: {
      type: Object,
      default: null
    },
    title: {
      type: String,
      default: '房间信息'
    },
    column: {
      type: Number,
      default: 3
    },
    realtimeStatus: {
      type: Object,
      default: null
    }
  },
  computed: {
    descriptionSpan() {
      return Math.max(1, Number(this.column) || 3)
    }
  },
  methods: {
    capacityText(row) {
      if (!row) return '-'
      if (row.capacityDesc) return row.capacityDesc
      if (row.capacityMin != null && row.capacityMax != null) return `${row.capacityMin}-${row.capacityMax}人`
      if (row.capacityMax != null) return `${row.capacityMax}人以内`
      return '-'
    }
  }
}
</script>

<style lang="scss" scoped>
.room-info-card {
  border-radius: 4px;
}

.room-info-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
