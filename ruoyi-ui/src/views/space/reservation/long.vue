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
            <el-select v-model="rule.roomId" filterable placeholder="请选择房间" style="width: 100%">
              <el-option v-for="room in rooms" :key="room.roomId" :label="roomLabel(room)" :value="room.roomId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="rule.startDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择开始日期" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="rule.endDate" value-format="yyyy-MM-dd" type="date" placeholder="请选择结束日期" style="width: 100%" />
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
import { formatDate, standardPeriods, weekdayText } from './utils'

export default {
  name: 'SpaceLongReservation',
  data() {
    return {
      rooms: [],
      periods: [],
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
  methods: {
    weekdayText,
    getRooms() {
      listRoom({ pageNum: 1, pageSize: 200, status: '0', bookable: '0' }).then(response => {
        this.rooms = response.rows || []
      })
    },
    getPeriods() {
      listTimePeriod({ pageNum: 1, pageSize: 10, status: '0' }).then(response => {
        this.periods = standardPeriods(response.rows)
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
        if (new Date(this.rule.startDate) > new Date(this.rule.endDate)) {
          this.$modal.msgWarning('开始日期不能晚于结束日期')
          done(false)
          return
        }
        done(true)
      })
    },
    buildItems() {
      const room = this.rooms.find(item => item.roomId === this.rule.roomId)
      const period = this.periods.find(item => item.periodId === this.rule.periodId)
      const rows = []
      const cur = new Date(this.rule.startDate)
      const end = new Date(this.rule.endDate)
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
      this.form.items = rows
      this.itemPager.pageNum = 1
      this.form.rule = {
        ...this.rule,
        weekdays: this.rule.weekdays.join(','),
        ruleDesc: `每周${this.rule.weekdays.map(weekdayText).join('/')} ${this.rule.startTime}-${this.rule.endTime}`
      }
      if (!rows.length) this.$modal.msgWarning('当前规则没有生成任何场次')
    },
    preview() {
      this.validateRule(valid => {
        if (!valid) return
        this.buildItems()
      })
    },
    submit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.validateRule(ruleValid => {
          if (!ruleValid) return
          this.buildItems()
          if (!this.form.items.length) return
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
