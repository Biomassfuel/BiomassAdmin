<template>
  <div class="app-container">

    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
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

    <el-form ref="ruleForm" :model="rule" :rules="ruleRules" label-width="110px">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="房间" prop="roomId">
            <el-select v-model="rule.roomId" filterable :loading="loadingRooms" placeholder="请选择房间" style="width: 100%" @visible-change="handleRoomSelectVisible">
              <el-option v-for="room in rooms" :key="room.roomId" :label="roomLabel(room)" :value="room.roomId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="rule.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" style="width: 100%" :picker-options="datePickerOptions" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="rule.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" style="width: 100%" :picker-options="datePickerOptions" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="重复星期" prop="weekdays">
            <el-checkbox-group v-model="rule.weekdays">
              <el-checkbox-button v-for="day in days" :key="day.value" :label="day.value">{{ day.label }}</el-checkbox-button>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预约时段" prop="periodId">
            <el-select v-model="rule.periodId" placeholder="请选择时段" style="width: 100%" @change="setRulePeriod">
              <el-option v-for="period in periods" :key="period.periodId" :label="periodLabel(period)" :value="period.periodId" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-date" size="mini" @click="preview">生成场次</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-check" size="mini" @click="submit">提交长期预约</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="el-icon-refresh" size="mini" @click="resetAll">重置</el-button>
      </el-col>
    </el-row>

    <el-table :data="pagedItems">
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
          <el-button type="text" size="mini" icon="el-icon-delete" @click="removeItem(scope.$index)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="form.items.length > itemPager.pageSize"
      :total="form.items.length"
      :page.sync="itemPager.pageNum"
      :limit.sync="itemPager.pageSize"
    />
  </div>
</template>

<script>
import { listRoom } from '@/api/space/room'
import { listTimePeriod } from '@/api/space/time-period'
import { addReservation } from '@/api/space/reservation'
import { fetchAllPages } from '@/utils/paged-list'
import { disablePastDate, findStartedReservation, formatDate, isBeforeToday, parseLocalDate, standardPeriods, weekdayText } from './utils'

export default {
  name: 'SpaceLongReservation',
  data() {
    return {
      loadingRooms: false,
      roomLoadSeq: 0,
      rooms: [],
      periods: [],
      datePickerOptions: {
        disabledDate: disablePastDate
      },
      days: [
        { value: '1', label: '周一' },
        { value: '2', label: '周二' },
        { value: '3', label: '周三' },
        { value: '4', label: '周四' },
        { value: '5', label: '周五' },
        { value: '6', label: '周六' },
        { value: '0', label: '周日' }
      ],
      rule: {
        ruleType: '0',
        roomId: null,
        startDate: null,
        endDate: null,
        periodId: null,
        weekdays: ['1'],
        startTime: null,
        endTime: null
      },
      form: {
        reservationType: '1',
        title: '',
        purpose: '课程教学',
        peopleCount: 1,
        detailRemark: '',
        items: [],
        rule: null
      },
      itemPager: { pageNum: 1, pageSize: 10 },
      rules: {
        title: [{ required: true, message: '预约主题不能为空', trigger: 'blur' }],
        purpose: [{ required: true, message: '预约用途不能为空', trigger: 'blur' }],
        peopleCount: [{ required: true, message: '预约人数不能为空', trigger: 'change' }]
      },
      ruleRules: {
        roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        weekdays: [{ type: 'array', required: true, message: '请选择重复星期', trigger: 'change' }],
        periodId: [{ required: true, message: '请选择预约时段', trigger: 'change' }]
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
        if (this.rule.roomId && !rows.some(item => item.roomId === this.rule.roomId)) {
          this.rule.roomId = null
          this.form.items = []
          this.form.rule = null
          this.itemPager.pageNum = 1
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
          this.rule.periodId = this.periods[0].periodId
          this.setRulePeriod(this.rule.periodId)
        }
      })
    },
    roomLabel(room) {
      return [room.roomCode, room.roomName, room.roomType, room.capacityDesc].filter(Boolean).join(' ')
    },
    periodLabel(period) {
      return `${period.periodName} ${period.startTime}-${period.endTime}`
    },
    setRulePeriod(periodId) {
      const period = this.periods.find(item => item.periodId === periodId)
      if (!period) return
      this.rule.startTime = period.startTime
      this.rule.endTime = period.endTime
      this.rule.periodName = period.periodName
    },
    validateRule(done) {
      this.$refs.ruleForm.validate(valid => {
        if (!valid) {
          done(false)
          return
        }
        if (parseLocalDate(this.rule.startDate) > parseLocalDate(this.rule.endDate)) {
          this.$modal.msgWarning('开始日期不能晚于结束日期')
          done(false)
          return
        }
        if (isBeforeToday(this.rule.startDate)) {
          this.$modal.msgWarning('开始日期不能早于今天')
          done(false)
          return
        }
        done(true)
      })
    },
    startedMessage(item) {
      const roomText = [item.roomCode, item.roomName].filter(Boolean).join(' ')
      return `${roomText ? roomText + ' ' : ''}${item.bookingDate} ${item.startTime} 的场次已开始，不能预约`
    },
    buildCandidateItems() {
      const room = this.rooms.find(item => item.roomId === this.rule.roomId)
      const period = this.periods.find(item => item.periodId === this.rule.periodId)
      const rows = []
      const cur = parseLocalDate(this.rule.startDate)
      const end = parseLocalDate(this.rule.endDate)
      while (cur <= end) {
        const weekday = String(cur.getDay())
        if (this.rule.weekdays.includes(weekday)) {
          rows.push({
            roomId: this.rule.roomId,
            roomCode: room ? room.roomCode : '',
            roomName: room ? room.roomName : '',
            periodId: this.rule.periodId,
            periodName: period ? period.periodName : '',
            bookingDate: formatDate(cur),
            weekday,
            startTime: this.rule.startTime,
            endTime: this.rule.endTime
          })
        }
        cur.setDate(cur.getDate() + 1)
      }
      return rows
    },
    clearGeneratedItems() {
      this.form.items = []
      this.form.rule = null
      this.itemPager.pageNum = 1
    },
    applyBuiltItems(rows) {
      this.form.items = rows
      this.itemPager.pageNum = 1
      this.form.rule = {
        ...this.rule,
        weekdays: this.rule.weekdays.join(','),
        ruleDesc: `每周${this.rule.weekdays.map(weekdayText).join('/')} ${this.rule.startTime}-${this.rule.endTime}`
      }
      if (!rows.length) this.$modal.msgWarning('当前规则没有生成任何场次')
      return rows.length > 0
    },
    buildItems() {
      const rows = this.buildCandidateItems()
      const started = findStartedReservation(rows)
      if (started) {
        this.clearGeneratedItems()
        this.$modal.msgWarning(this.startedMessage(started))
        return false
      }
      return this.applyBuiltItems(rows)
    },
    preview() {
      this.validateRule(valid => {
        if (!valid) return
        this.buildItems()
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
        this.validateRule(ruleValid => {
          if (!ruleValid) return
          if (!this.form.items.length) {
            this.$modal.msgWarning('请先生成场次')
            return
          }
          const started = findStartedReservation(this.form.items)
          if (started) {
            this.$modal.msgWarning(this.startedMessage(started))
            return
          }
          addReservation(this.form).then(() => {
            this.$modal.msgSuccess('长期预约已提交')
            this.resetAll()
          })
        })
      })
    },
    resetAll() {
      this.rule = { ruleType: '0', roomId: null, startDate: null, endDate: null, periodId: this.periods[0] && this.periods[0].periodId, weekdays: ['1'], startTime: null, endTime: null }
      if (this.rule.periodId) this.setRulePeriod(this.rule.periodId)
      this.form = { reservationType: '1', title: '', purpose: '课程教学', peopleCount: 1, detailRemark: '', items: [], rule: null }
      this.itemPager.pageNum = 1
      this.resetForm('form')
      this.$nextTick(() => {
        if (this.$refs.ruleForm) this.$refs.ruleForm.clearValidate()
      })
    }
  }
}
</script>
