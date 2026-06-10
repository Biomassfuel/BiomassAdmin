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
      <el-form-item label="房间类型" prop="roomType">
        <el-input v-model="queryParams.roomType" placeholder="请输入房间类型" clearable @keyup.enter.native="handleQuery" />
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="roomList">
      <el-table-column label="房间编号" prop="roomCode" align="center" width="120" />
      <el-table-column label="房间名称" prop="roomName" align="center" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="楼栋" prop="buildingName" align="center" width="110" />
      <el-table-column label="楼层" prop="floorNo" align="center" width="90" />
      <el-table-column label="类型" prop="roomType" align="center" width="120" />
      <el-table-column label="容量" align="center" min-width="130">
        <template slot-scope="scope">{{ capacityText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="可预约" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.bookable === '0' ? 'success' : 'info'">
            {{ scope.row.bookable === '0' ? '可预约' : '不可预约' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="90" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['space:room:query']"
          >详情</el-button>
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
  </div>
</template>

<script>
import { listRoom } from '@/api/space/room'

export default {
  name: 'SpaceOccupancy',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      roomList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roomCode: null,
        roomName: null,
        buildingName: null,
        roomType: null,
        bookable: null,
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
    getList() {
      this.loading = true
      listRoom(this.queryParams).then(response => {
        this.roomList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    capacityText(row) {
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
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleDetail(row) {
      this.$router.push({
        path: `/space-reservation/occupancy-detail/index/${row.roomId}`
      })
    }
  }
}
</script>
