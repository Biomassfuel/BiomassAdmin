<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="78px">
      <el-form-item label="房间编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房间编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房间名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="楼栋" prop="buildingName">
        <el-input v-model="queryParams.buildingName" placeholder="请输入楼栋" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="房间类型" prop="typeId">
        <el-select v-model="queryParams.typeId" placeholder="请选择房间类型" clearable filterable>
          <el-option v-for="item in typeOptions" :key="item.typeId" :label="item.typeName" :value="item.typeId" />
        </el-select>
      </el-form-item>
      <el-form-item label="可预约" prop="bookable">
        <el-select v-model="queryParams.bookable" placeholder="全部" clearable>
          <el-option label="可预约" value="0" />
          <el-option label="不可预约" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['space:room:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['space:room:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['space:room:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport" v-hasPermi="['space:room:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['space:room:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-delete" size="mini" @click="handleRecycle" v-hasPermi="['space:room:list']">回收站</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roomList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="房间编号" align="center" prop="roomCode" width="120" />
      <el-table-column label="房间名称" align="center" prop="roomName" :show-overflow-tooltip="true" />
      <el-table-column label="楼栋" align="center" prop="buildingName" width="110" />
      <el-table-column label="楼层" align="center" prop="floorNo" width="90" />
      <el-table-column label="类型" align="center" prop="roomType" width="120" />
      <el-table-column label="容量" align="center" min-width="130">
        <template slot-scope="scope">{{ capacityText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="可预约" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.bookable"
            active-value="0"
            inactive-value="1"
            @change="handleBookableChange(scope.row)"
            v-hasPermi="['space:room:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
            v-hasPermi="['space:room:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="归属单位" align="center" prop="assignedOrgName" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)" v-hasPermi="['space:room:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-tickets" @click="handleReservationRecords(scope.row)" v-hasPermi="['space:reservationItem:publicList']">预约记录</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['space:room:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['space:room:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="920px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="108px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="房间编号" prop="roomCode">
              <el-input v-model="form.roomCode" placeholder="请输入房间编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间名称" prop="roomName">
              <el-input v-model="form.roomName" placeholder="请输入房间名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼栋名称" prop="buildingName">
              <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floorNo">
              <el-input v-model="form.floorNo" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="typeId">
              <el-select v-model="form.typeId" placeholder="请选择房间类型" filterable clearable style="width: 100%" @change="handleTypeChange">
                <el-option v-for="item in typeOptions" :key="item.typeId" :label="item.typeName" :value="item.typeId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积">
              <el-input-number v-model="form.area" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最小容量">
              <el-input-number v-model="form.capacityMin" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大容量">
              <el-input-number v-model="form.capacityMax" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属单位">
              <el-input v-model="form.assignedOrgName" placeholder="请输入归属单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可预约" prop="bookable">
              <el-radio-group v-model="form.bookable">
                <el-radio label="0">可预约</el-radio>
                <el-radio label="1">不可预约</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容量说明">
              <el-input v-model="form.capacityDesc" placeholder="请输入容量说明" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备说明">
              <el-input v-model="form.equipmentDesc" placeholder="请输入设备说明" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设备明细">
              <div class="room-equipment-editor">
                <el-table :data="form.roomEquipmentList || []" border size="mini" empty-text="暂无设备明细">
                  <el-table-column label="设备" min-width="220">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.equipmentId" placeholder="请选择设备" filterable clearable style="width: 100%" @change="handleRoomEquipmentChange(scope.row)">
                        <el-option
                          v-for="item in equipmentOptions"
                          :key="item.equipmentId"
                          :label="item.equipmentName"
                          :value="item.equipmentId"
                          :disabled="isEquipmentSelected(item.equipmentId, scope.$index)"
                        />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="数量" width="140">
                    <template slot-scope="scope">
                      <el-input-number v-model="scope.row.quantity" :min="1" :precision="0" controls-position="right" style="width: 100%" />
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="140">
                    <template slot-scope="scope">
                      <el-select v-model="scope.row.status" placeholder="请选择" style="width: 100%">
                        <el-option label="正常" value="0" />
                        <el-option label="停用" value="1" />
                      </el-select>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="90" align="center">
                    <template slot-scope="scope">
                      <el-button type="text" icon="el-icon-delete" @click="removeRoomEquipment(scope.$index)">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-button class="room-equipment-editor__add" type="primary" plain size="mini" icon="el-icon-plus" @click="addRoomEquipment">添加设备</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="位置说明">
              <el-input v-model="form.locationDesc" type="textarea" placeholder="请输入位置说明" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="房间详情" :visible.sync="detailOpen" width="92vw" append-to-body class="room-detail-dialog">
      <div class="room-detail-dialog__body">
        <div class="room-detail-dialog__actions">
          <el-button
            type="primary"
            plain
            size="mini"
            icon="el-icon-tickets"
            :disabled="!detail || !detail.roomId"
            @click="handleReservationRecords(detail)"
            v-hasPermi="['space:reservationItem:publicList']"
          >预约记录</el-button>
        </div>
        <room-info-card :room="detail" :column="3" :realtime-status="detailRealtimeStatus" />
        <room-week-schedule :room-id="detail && detail.roomId" />
      </div>
    </el-dialog>

    <el-dialog :title="reservationRecordTitle" :visible.sync="reservationRecordOpen" width="92vw" append-to-body>
      <public-reservation-items v-if="reservationRecordOpen" :room-id="reservationRecordRoomId" :toolbar="false" fixed-item-status="2" />
    </el-dialog>

    <el-dialog title="房间回收站" :visible.sync="recycleOpen" width="1100px" append-to-body>
      <el-table v-loading="recycleLoading" :data="recycleList">
        <el-table-column label="房间编号" align="center" prop="roomCode" width="120" />
        <el-table-column label="房间名称" align="center" prop="roomName" :show-overflow-tooltip="true" />
        <el-table-column label="楼栋" align="center" prop="buildingName" width="110" />
        <el-table-column label="楼层" align="center" prop="floorNo" width="90" />
        <el-table-column label="类型" align="center" prop="roomType" width="120" />
        <el-table-column label="容量" align="center" min-width="120">
          <template slot-scope="scope">{{ capacityText(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="归属单位" align="center" prop="assignedOrgName" :show-overflow-tooltip="true" />
        <el-table-column label="删除时间" align="center" prop="updateTime" width="160" />
        <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-refresh-left" @click="handleRestore(scope.row)" v-hasPermi="['space:room:edit']">恢复</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleForceDelete(scope.row)" v-hasPermi="['space:room:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="recycleTotal > 0"
        :total="recycleTotal"
        :page.sync="recycleQueryParams.pageNum"
        :limit.sync="recycleQueryParams.pageSize"
        @pagination="getRecycleList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="recycleOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div>
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的房间编号
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { listRoom, listRecycleRoom, getRoom, addRoom, updateRoom, delRoom, restoreRoom, forceDelRoom } from '@/api/space/room'
import { listRoomType } from '@/api/space/room-type'
import { listEquipment } from '@/api/space/equipment'
import { listPublicReservationItem } from '@/api/space/reservation-item'
import { fetchAllPages } from '@/utils/paged-list'
import { formatDate } from '@/views/space/reservation/utils'
import RoomInfoCard from '@/views/space/components/RoomInfoCard'
import RoomWeekSchedule from '@/views/space/components/RoomWeekSchedule'
import PublicReservationItems from '@/views/space/reservation/PublicReservationItems'

export default {
  name: 'SpaceRoom',
  components: { RoomInfoCard, RoomWeekSchedule, PublicReservationItems },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      roomList: [],
      typeOptions: [],
      equipmentOptions: [],
      title: '',
      open: false,
      detailOpen: false,
      reservationRecordOpen: false,
      recycleOpen: false,
      recycleLoading: false,
      detail: null,
      reservationRecordRoomId: null,
      reservationRecordTitle: '预约记录',
      recycleList: [],
      recycleTotal: 0,
      detailRealtimeStatus: {
        occupied: false,
        text: '空闲'
      },
      upload: {
        open: false,
        title: '',
        isUploading: false,
        updateSupport: false,
        headers: { Authorization: 'Bearer ' + getToken() },
        url: process.env.VUE_APP_BASE_API + '/space/room/importData'
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roomCode: null,
        roomName: null,
        buildingName: null,
        typeId: null,
        bookable: null,
        status: null
      },
      recycleQueryParams: {
        pageNum: 1,
        pageSize: 10
      },
      form: {},
      rules: {
        roomCode: [{ required: true, message: '房间编号不能为空', trigger: 'blur' }],
        roomName: [{ required: true, message: '房间名称不能为空', trigger: 'blur' }],
        buildingName: [{ required: true, message: '楼栋名称不能为空', trigger: 'blur' }],
        floorNo: [{ required: true, message: '楼层不能为空', trigger: 'blur' }],
        typeId: [{ required: true, message: '房间类型不能为空', trigger: 'change' }],
        bookable: [{ required: true, message: '请选择是否可预约', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getTypeOptions()
    this.getEquipmentOptions()
    this.getList()
  },
  activated() {
    this.getTypeOptions()
    this.getEquipmentOptions()
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listRoom(this.queryParams).then(response => {
        this.roomList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getRecycleList() {
      this.recycleLoading = true
      listRecycleRoom(this.recycleQueryParams).then(response => {
        this.recycleList = response.rows
        this.recycleTotal = response.total
        this.recycleLoading = false
      })
    },
    getTypeOptions() {
      fetchAllPages(listRoomType, { status: '0' }).then(rows => {
        this.typeOptions = rows
      })
    },
    getEquipmentOptions() {
      fetchAllPages(listEquipment, { status: '0' }).then(rows => {
        this.equipmentOptions = rows
      })
    },
    capacityText(row) {
      if (row.capacityDesc) return row.capacityDesc
      if (row.capacityMin != null && row.capacityMax != null) return `${row.capacityMin}-${row.capacityMax}人`
      if (row.capacityMax != null) return `${row.capacityMax}人以内`
      return '-'
    },
    handleTypeChange(typeId) {
      const type = this.typeOptions.find(item => item.typeId === typeId)
      this.form.roomType = type ? type.typeName : null
    },
    normalizeRoomEquipmentList(list) {
      return (list || []).map(item => ({
        roomEquipmentId: item.roomEquipmentId || null,
        roomId: item.roomId || this.form.roomId || null,
        equipmentId: item.equipmentId || null,
        equipmentCode: item.equipmentCode || null,
        equipmentName: item.equipmentName || null,
        quantity: item.quantity || 1,
        status: item.status || '0',
        remark: item.remark || null
      }))
    },
    addRoomEquipment() {
      if (!this.form.roomEquipmentList) {
        this.$set(this.form, 'roomEquipmentList', [])
      }
      this.form.roomEquipmentList.push({
        roomEquipmentId: null,
        roomId: this.form.roomId || null,
        equipmentId: null,
        equipmentCode: null,
        equipmentName: null,
        quantity: 1,
        status: '0',
        remark: null
      })
    },
    removeRoomEquipment(index) {
      this.form.roomEquipmentList.splice(index, 1)
    },
    handleRoomEquipmentChange(row) {
      const equipment = this.equipmentOptions.find(item => item.equipmentId === row.equipmentId)
      row.equipmentCode = equipment ? equipment.equipmentCode : null
      row.equipmentName = equipment ? equipment.equipmentName : null
    },
    isEquipmentSelected(equipmentId, currentIndex) {
      return (this.form.roomEquipmentList || []).some((item, index) => index !== currentIndex && item.equipmentId === equipmentId)
    },
    validateRoomEquipmentList() {
      const selectedIds = (this.form.roomEquipmentList || []).filter(item => item.equipmentId).map(item => item.equipmentId)
      if (new Set(selectedIds).size !== selectedIds.length) {
        this.$modal.msgError('同一房间不能重复选择同一个设备')
        return false
      }
      return true
    },
    buildRoomPayload() {
      const payload = {
        ...this.form,
        roomEquipmentList: this.normalizeRoomEquipmentList(this.form.roomEquipmentList)
          .filter(item => item.equipmentId)
          .map(item => ({
            ...item,
            quantity: item.quantity || 1,
            status: item.status || '0'
          }))
      }
      return payload
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        roomId: null,
        roomCode: null,
        roomName: null,
        buildingId: null,
        buildingName: '6#楼',
        floorNo: null,
        typeId: null,
        roomType: null,
        area: null,
        capacityMin: null,
        capacityMax: null,
        capacityDesc: null,
        assignedOrgId: null,
        assignedOrgName: null,
        equipmentDesc: null,
        roomEquipmentList: [],
        locationDesc: null,
        bookable: '0',
        status: '0',
        remark: null
      }
      this.resetForm('form')
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
      this.ids = selection.map(item => item.roomId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加房间'
    },
    handleUpdate(row) {
      this.reset()
      const roomId = row && row.roomId ? row.roomId : this.ids[0]
      getRoom(roomId).then(response => {
        this.form = response.data
        this.form.roomEquipmentList = this.normalizeRoomEquipmentList(response.data.roomEquipmentList)
        this.open = true
        this.title = '修改房间'
      })
    },
    handleDetail(row) {
      this.detail = null
      this.detailRealtimeStatus = { occupied: false, text: '空闲' }
      this.detailOpen = true
      getRoom(row.roomId).then(response => {
        this.detail = response.data
      })
      this.getDetailRealtimeStatus(row.roomId)
    },
    handleReservationRecords(row) {
      if (!row || !row.roomId) return
      this.reservationRecordRoomId = row.roomId
      this.reservationRecordTitle = `${row.roomCode || ''} ${row.roomName || ''} 预约记录`.trim()
      this.reservationRecordOpen = true
    },
    getDetailRealtimeStatus(roomId) {
      if (!roomId) return
      const now = new Date()
      listPublicReservationItem({
        pageNum: 1,
        pageSize: 500,
        roomId,
        bookingDate: formatDate(now)
      }).then(response => {
        const currentMinutes = this.timeToMinutes(`${now.getHours()}:${now.getMinutes()}`)
        const activeItem = (response.rows || []).find(item => {
          if (!['1', '2'].includes(item.itemStatus)) return false
          const start = this.timeToMinutes(item.startTime)
          const end = this.timeToMinutes(item.endTime)
          return start <= currentMinutes && currentMinutes < end
        })
        this.detailRealtimeStatus = activeItem
          ? { occupied: true, text: `占用中 ${activeItem.startTime} - ${activeItem.endTime}` }
          : { occupied: false, text: '空闲' }
      })
    },
    timeToMinutes(value) {
      const parts = String(value || '0:0').split(':')
      return Number(parts[0] || 0) * 60 + Number(parts[1] || 0)
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (!this.validateRoomEquipmentList()) return
        const payload = this.buildRoomPayload()
        if (this.form.roomId != null) {
          updateRoom(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addRoom(payload).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const roomIds = row && row.roomId ? row.roomId : this.ids
      this.$modal.confirm('是否确认删除房间编号为"' + roomIds + '"的数据项？').then(function() {
        return delRoom(roomIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleRecycle() {
      this.recycleQueryParams.pageNum = 1
      this.recycleOpen = true
      this.getRecycleList()
    },
    handleRestore(row) {
      this.$modal.confirm('是否确认恢复房间编号为"' + row.roomCode + '"的数据项？').then(() => {
        return restoreRoom(row.roomId)
      }).then(() => {
        this.getRecycleList()
        this.getList()
        this.$modal.msgSuccess('恢复成功')
      }).catch(() => {})
    },
    handleForceDelete(row) {
      this.$modal.confirm('永久删除后不可恢复，是否确认永久删除房间编号为"' + row.roomCode + '"的数据项？').then(() => {
        return forceDelRoom(row.roomId)
      }).then(() => {
        this.getRecycleList()
        this.$modal.msgSuccess('永久删除成功')
      }).catch(() => {})
    },
    handleBookableChange(row) {
      const text = row.bookable === '0' ? '设为可预约' : '设为不可预约'
      this.$modal.confirm('确认要' + text + '房间"' + row.roomCode + '"吗？').then(function() {
        return updateRoom(row)
      }).then(() => {
        this.$modal.msgSuccess('设置成功')
      }).catch(() => {
        row.bookable = row.bookable === '0' ? '1' : '0'
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$modal.confirm('确认要' + text + '房间"' + row.roomCode + '"吗？').then(function() {
        return updateRoom(row)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    handleImport() {
      this.upload.title = '房间批量导入'
      this.upload.open = true
    },
    importTemplate() {
      this.download('space/room/importTemplate', {}, `room_template_${new Date().getTime()}.xlsx`)
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess(response) {
      this.upload.open = false
      this.upload.isUploading = false
      this.$refs.upload.clearFiles()
      this.$alert("<div style='overflow:auto;overflow-x:hidden;max-height:70vh;padding:10px 20px 0;'>" + response.msg + '</div>', '导入结果', { dangerouslyUseHTMLString: true })
      this.getList()
    },
    submitFileForm() {
      this.$refs.upload.submit()
    },
    handleExport() {
      this.download('space/room/export', {
        ...this.queryParams
      }, `room_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style lang="scss" scoped>
.room-equipment-editor {
  width: 100%;
}

.room-equipment-editor__add {
  margin-top: 8px;
}

.room-detail-dialog__body {
  max-height: 72vh;
  overflow-y: auto;
  padding-right: 4px;
}

.room-detail-dialog__actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}
</style>
