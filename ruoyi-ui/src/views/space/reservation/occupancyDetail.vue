<template>
  <div class="app-container">
    <div class="detail-toolbar">
      <el-button plain icon="el-icon-back" size="mini" @click="handleBack">返回</el-button>
    </div>

    <room-info-card :room="room" :realtime-status="realtimeStatus" :column="3" />

    <room-week-schedule :room-id="queryParams.roomId" />
  </div>
</template>

<script>
import { getRoom } from '@/api/space/room'
import { listReservationItem } from '@/api/space/reservation-item'
import { formatDate } from './utils'
import RoomInfoCard from '@/views/space/components/RoomInfoCard'
import RoomWeekSchedule from '@/views/space/components/RoomWeekSchedule'

export default {
  name: 'SpaceOccupancyDetail',
  components: { RoomInfoCard, RoomWeekSchedule },
  data() {
    return {
      room: null,
      hasActivated: false,
      realtimeStatus: {
        occupied: false,
        text: '空闲'
      },
      queryParams: {
        roomId: null
      }
    }
  },
  watch: {
    '$route.params.roomId'(roomId, oldRoomId) {
      if (roomId && roomId !== oldRoomId) {
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
    loadPage() {
      this.initRoomId()
      this.getRoomInfo()
      this.getRealtimeStatus()
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
    getRealtimeStatus() {
      if (!this.queryParams.roomId) return
      const now = new Date()
      listReservationItem({
        pageNum: 1,
        pageSize: 500,
        roomId: this.queryParams.roomId,
        bookingDate: formatDate(now),
        occupiedOnly: true
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

</style>
