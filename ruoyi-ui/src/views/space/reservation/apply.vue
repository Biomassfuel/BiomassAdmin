<template>
  <div class="app-container">

    <el-form ref="form" :model="form" :rules="rules" label-width="96px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="预约主题" prop="title">
            <el-input v-model="form.title" placeholder="请输入预约主题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预约用途" prop="purpose">
            <el-input v-model="form.purpose" placeholder="请输入预约用途" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="预约人数" prop="peopleCount">
            <el-input-number v-model="form.peopleCount" :min="1" controls-position="right" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-form-item label="备注">
            <el-input v-model="form.detailRemark" placeholder="请输入备注" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-divider content-position="center">预约场次</el-divider>

    <el-form ref="itemForm" :model="itemForm" :rules="itemRules" size="small" :inline="true" label-width="78px">
      <el-form-item label="房间" prop="roomId">
        <el-select v-model="itemForm.roomId" filterable :loading="loadingRooms" placeholder="请选择房间" style="width: 260px" @visible-change="handleRoomSelectVisible">
          <el-option v-for="room in rooms" :key="room.roomId" :label="roomLabel(room)" :value="room.roomId" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期" prop="bookingDate">
        <el-date-picker v-model="itemForm.bookingDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择日期" :picker-options="datePickerOptions" />
      </el-form-item>
      <el-form-item label="时段" prop="periodId">
        <el-select v-model="itemForm.periodId" placeholder="请选择时段" style="width: 220px" @change="setItemPeriod">
          <el-option v-for="period in periods" :key="period.periodId" :label="periodLabel(period)" :value="period.periodId" :disabled="isPeriodStarted(period)" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="addItem">添加场次</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loadingRooms" :data="pagedItems">
      <el-table-column label="房间" align="center" min-width="160">
        <template slot-scope="scope">{{ scope.row.roomCode }} {{ scope.row.roomName }}</template>
      </el-table-column>
      <el-table-column label="日期" prop="bookingDate" align="center" width="120" />
      <el-table-column label="星期" align="center" width="90">
        <template slot-scope="scope">{{ weekdayText(scope.row.weekday) }}</template>
      </el-table-column>
      <el-table-column label="时段" align="center" width="180">
        <template slot-scope="scope">{{ scope.row.periodName }} {{ scope.row.startTime }}-{{ scope.row.endTime }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="90" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="text" size="mini" icon="el-icon-delete" @click="removeItem(scope.$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="form.items.length > itemPager.pageSize"
      :total="form.items.length"
      :page.sync="itemPager.pageNum"
      :limit.sync="itemPager.pageSize"
    />

    <div class="submit-bar">
      <el-button type="primary" icon="el-icon-check" @click="submit">提交预约</el-button>
      <el-button icon="el-icon-refresh" @click="resetAll">重置</el-button>
    </div>
  </div>
</template>

<script>
import { listRoom } from '@/api/space/room'
import { listTimePeriod } from '@/api/space/time-period'
import { addReservation } from '@/api/space/reservation'
import { fetchAllPages } from '@/utils/paged-list'
import { disablePastDate, findStartedReservation, isReservationStarted, standardPeriods, weekdayText, weekdayValue } from './utils'

export default {
  name: 'SpaceReservationApply',
  data() {
    return {
      loadingRooms: false,
      roomLoadSeq: 0,
      rooms: [],
      periods: [],
      datePickerOptions: {
        disabledDate: disablePastDate
      },
      form: {
        reservationType: '0',
        title: '',
        purpose: '',
        peopleCount: 1,
        detailRemark: '',
        items: []
      },
      itemForm: {
        roomId: null,
        bookingDate: null,
        periodId: null,
        startTime: null,
        endTime: null
      },
      itemPager: { pageNum: 1, pageSize: 10 },
      rules: {
        title: [{ required: true, message: '预约主题不能为空', trigger: 'blur' }],
        purpose: [{ required: true, message: '预约用途不能为空', trigger: 'blur' }],
        peopleCount: [{ required: true, message: '预约人数不能为空', trigger: 'change' }]
      },
      itemRules: {
        roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
        bookingDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
        periodId: [{ required: true, message: '请选择时段', trigger: 'change' }]
      }
    }
  },
  computed: {
    pagedItems() {
      const start = (this.itemPager.pageNum - 1) * this.itemPager.pageSize
      return this.form.items.slice(start, start + this.itemPager.pageSize)
    }
  },
  created() {
    this.getRooms()
    this.getPeriods()
  },
  activated() {
    this.getRooms()
  },
  methods: {
    weekdayText,
    getRooms() {
      const seq = ++this.roomLoadSeq
      this.loadingRooms = true
      fetchAllPages(listRoom, { status: '0', bookable: '0' }).then(rows => {
        if (seq !== this.roomLoadSeq) return
        this.rooms = rows
        if (this.itemForm.roomId && !rows.some(item => item.roomId === this.itemForm.roomId)) {
          this.itemForm.roomId = null
        }
        this.loadingRooms = false
      }).catch(() => {
        if (seq !== this.roomLoadSeq) return
        this.loadingRooms = false
      })
    },
    handleRoomSelectVisible(visible) {
      if (visible) this.getRooms()
    },
    getPeriods() {
      fetchAllPages(listTimePeriod, { status: '0' }).then(rows => {
        this.periods = standardPeriods(rows)
        if (this.periods.length) {
          this.itemForm.periodId = this.periods[0].periodId
          this.setItemPeriod(this.itemForm.periodId)
        }
      })
    },
    roomLabel(room) {
      return [room.roomCode, room.roomName, room.roomType, room.capacityDesc].filter(Boolean).join(' ')
    },
    periodLabel(period) {
      return `${period.periodName} ${period.startTime}-${period.endTime}`
    },
    isPeriodStarted(period) {
      return isReservationStarted({
        bookingDate: this.itemForm.bookingDate,
        startTime: period.startTime
      })
    },
    startedMessage(item) {
      const roomText = [item.roomCode, item.roomName].filter(Boolean).join(' ')
      return `${roomText ? roomText + ' ' : ''}${item.bookingDate} ${item.startTime} 的场次已开始，不能预约`
    },
    setItemPeriod(periodId) {
      const period = this.periods.find(item => item.periodId === periodId)
      if (!period) return
      this.itemForm.startTime = period.startTime
      this.itemForm.endTime = period.endTime
      this.itemForm.periodName = period.periodName
    },
    addItem() {
      this.$refs.itemForm.validate(valid => {
        if (!valid) return
        const room = this.rooms.find(item => item.roomId === this.itemForm.roomId)
        const period = this.periods.find(item => item.periodId === this.itemForm.periodId)
        const currentItem = {
          ...this.itemForm,
          roomCode: room ? room.roomCode : '',
          roomName: room ? room.roomName : '',
          periodName: period ? period.periodName : ''
        }
        if (isReservationStarted(currentItem)) {
          this.$modal.msgWarning(this.startedMessage(currentItem))
          return
        }
        const duplicate = this.form.items.some(item => {
          return item.roomId === this.itemForm.roomId && item.bookingDate === this.itemForm.bookingDate && item.periodId === this.itemForm.periodId
        })
        if (duplicate) {
          this.$modal.msgWarning('该房间、日期和时段已添加')
          return
        }
        this.form.items.push({
          ...currentItem,
          weekday: weekdayValue(this.itemForm.bookingDate)
        })
        this.itemPager.pageNum = Math.ceil(this.form.items.length / this.itemPager.pageSize)
      })
    },
    removeItem(pageIndex) {
      const index = (this.itemPager.pageNum - 1) * this.itemPager.pageSize + pageIndex
      this.form.items.splice(index, 1)
      const maxPage = Math.max(1, Math.ceil(this.form.items.length / this.itemPager.pageSize))
      this.itemPager.pageNum = Math.min(this.itemPager.pageNum, maxPage)
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (!this.form.items.length) {
          this.$modal.msgWarning('请至少添加一个场次')
          return
        }
        const started = findStartedReservation(this.form.items)
        if (started) {
          this.$modal.msgWarning(this.startedMessage(started))
          return
        }
        addReservation(this.form).then(() => {
          this.$modal.msgSuccess('预约已提交')
          this.resetAll()
        })
      })
    },
    resetAll() {
      this.form = { reservationType: '0', title: '', purpose: '', peopleCount: 1, detailRemark: '', items: [] }
      this.itemForm = { roomId: null, bookingDate: null, periodId: this.periods[0] && this.periods[0].periodId, startTime: null, endTime: null }
      if (this.itemForm.periodId) this.setItemPeriod(this.itemForm.periodId)
      this.itemPager.pageNum = 1
      this.resetForm('form')
      this.resetForm('itemForm')
    }
  }
}
</script>

<style lang="scss" scoped>
.submit-bar {
  margin-top: 16px;
  text-align: right;
}
</style>
