<template>
  <div class="app-container gen-page">
    <div class="gen-hero">
      <div class="gen-hero__content">
        <h1 class="gen-hero__title">代码生成</h1>
        <p class="gen-hero__desc">导入数据库表结构，配置字段规则后生成前后端代码。</p>
      </div>
      <div class="gen-hero__stats">
        <div class="gen-stat-card">
          <span>{{ total }}</span>
          <label>已导入表</label>
        </div>
        <div class="gen-stat-card">
          <span>{{ ids.length }}</span>
          <label>当前选中</label>
        </div>
      </div>
    </div>

    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
      class="gen-search-panel"
    >
      <el-form-item label="表名称" prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="表描述" prop="tableComment">
        <el-input
          v-model="queryParams.tableComment"
          placeholder="请输入表描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          class="gen-date-range"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item class="gen-search-actions">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <section class="gen-table-panel">
      <div class="gen-table-panel__header">
        <div>
          <h2>数据表列表</h2>
          <p>选择表后可批量生成、同步结构或进入生成配置。</p>
        </div>
        <div class="gen-table-actions">
          <el-button
            type="primary"
            plain
            icon="el-icon-download"
            size="mini"
            :disabled="multiple"
            @click="handleGenTable"
            v-hasPermi="['tool:gen:code']"
          >生成</el-button>
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="openCreateTable"
            v-hasRole="['admin']"
          >创建</el-button>
          <el-button
            type="info"
            plain
            icon="el-icon-upload"
            size="mini"
            @click="openImportTable"
            v-hasPermi="['tool:gen:import']"
          >导入</el-button>
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleEditTable"
            v-hasPermi="['tool:gen:edit']"
          >修改</el-button>
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['tool:gen:remove']"
          >删除</el-button>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </div>

      <el-table
        ref="tables"
        v-loading="loading"
        :data="tableList"
        class="gen-table"
        @selection-change="handleSelectionChange"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
      >
        <template slot="empty">
          <div class="gen-empty">
            <div class="gen-empty__icon"><i class="el-icon-document"></i></div>
            <div class="gen-empty__title">暂无数据表</div>
            <div class="gen-empty__desc">导入数据库表后，即可维护生成配置并生成代码。</div>
            <el-button type="primary" size="mini" icon="el-icon-upload" @click="openImportTable" v-hasPermi="['tool:gen:import']">导入表</el-button>
          </div>
        </template>
        <el-table-column type="selection" align="center" width="48"></el-table-column>
        <el-table-column label="序号" type="index" width="64" align="center">
          <template slot-scope="scope">
            <span class="gen-index">{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column label="表名称" align="left" prop="tableName" :show-overflow-tooltip="true" min-width="160">
          <template slot-scope="scope">
            <span class="gen-table-name">{{ scope.row.tableName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="表描述" align="left" prop="tableComment" :show-overflow-tooltip="true" min-width="180" />
        <el-table-column label="实体" align="left" prop="className" :show-overflow-tooltip="true" min-width="160" />
        <el-table-column label="创建时间" align="left" prop="createTime" sortable="custom" :sort-orders="['descending', 'ascending']" width="170" />
        <el-table-column label="更新时间" align="left" prop="updateTime" sortable="custom" :sort-orders="['descending', 'ascending']" width="170" />
        <el-table-column label="操作" align="center" class-name="gen-table-ops" width="300">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              icon="el-icon-view"
              @click="handlePreview(scope.row)"
              v-hasPermi="['tool:gen:preview']"
            >预览</el-button>
            <el-button
              type="text"
              size="small"
              icon="el-icon-edit"
              @click="handleEditTable(scope.row)"
              v-hasPermi="['tool:gen:edit']"
            >编辑</el-button>
            <el-button
              type="text"
              size="small"
              icon="el-icon-refresh"
              @click="handleSynchDb(scope.row)"
              v-hasPermi="['tool:gen:edit']"
            >同步</el-button>
            <el-button
              type="text"
              size="small"
              icon="el-icon-download"
              @click="handleGenTable(scope.row)"
              v-hasPermi="['tool:gen:code']"
            >生成</el-button>
            <el-button
              type="text"
              size="small"
              icon="el-icon-delete"
              class="is-danger"
              @click="handleDelete(scope.row)"
              v-hasPermi="['tool:gen:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </section>
    <!-- 预览界面 -->
    <el-dialog :title="preview.title" :visible.sync="preview.open" width="80%" top="5vh" append-to-body class="scrollbar gen-preview-dialog">
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :name="key.substring(key.lastIndexOf('/')+1,key.indexOf('.vm'))"
          :key="key"
        >
          <el-link :underline="false" icon="el-icon-document-copy" v-clipboard:copy="value" v-clipboard:success="clipboardSuccess" style="float:right">复制</el-link>
          <pre><code class="hljs" v-html="highlightedCode(value, key)"></code></pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table ref="import" @ok="handleQuery" />
    <create-table ref="create" @ok="handleQuery" />
  </div>
</template>

<script>
import { listTable, previewTable, delTable, genCode, synchDb } from "@/api/tool/gen"
import importTable from "./importTable"
import createTable from "./createTable"
import hljs from "highlight.js/lib/highlight"
import "highlight.js/styles/github-gist.css"
hljs.registerLanguage("java", require("highlight.js/lib/languages/java"))
hljs.registerLanguage("xml", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("html", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("vue", require("highlight.js/lib/languages/xml"))
hljs.registerLanguage("javascript", require("highlight.js/lib/languages/javascript"))
hljs.registerLanguage("typescript", require("highlight.js/lib/languages/typescript"))
hljs.registerLanguage("sql", require("highlight.js/lib/languages/sql"))

export default {
  name: "Gen",
  components: { importTable, createTable },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 唯一标识符
      uniqueId: "",
      // 选中数组
      ids: [],
      // 选中表数组
      tableNames: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 表数据
      tableList: [],
      // 日期范围
      dateRange: "",
      // 默认排序
      defaultSort: { prop: "createTime", order: "descending" },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tableName: undefined,
        tableComment: undefined
      },
      // 预览参数
      preview: {
        open: false,
        title: "代码预览",
        data: {},
        activeName: "domain.java"
      }
    }
  },
  created() {
    this.queryParams.orderByColumn = this.defaultSort.prop
    this.queryParams.isAsc = this.defaultSort.order
    this.getList()
  },
  activated() {
    const time = this.$route.query.t
    if (time != null && time != this.uniqueId) {
      this.uniqueId = time
      this.queryParams.pageNum = Number(this.$route.query.pageNum)
      this.getList()
    }
  },
  methods: {
    /** 查询表集合 */
    getList() {
      this.loading = true
      listTable(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.tableList = response.rows
          this.total = response.total
          this.loading = false
        }
      )
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 生成代码操作 */
    handleGenTable(row) {
      const tableNames = row.tableName || this.tableNames
      if (tableNames == "") {
        this.$modal.msgError("请选择要生成的数据")
        return
      }
      if(row.genType === "1") {
        genCode(row.tableName).then(() => {
          this.$modal.msgSuccess("成功生成到自定义路径：" + row.genPath)
        })
      } else {
        const zipName = Array.isArray(tableNames) ? "biomass.zip" : tableNames + ".zip"
        this.$download.zip("/tool/gen/batchGenCode?tables=" + tableNames, zipName)
      }
    },
    /** 同步数据库操作 */
    handleSynchDb(row) {
      const tableName = row.tableName
      this.$modal.confirm('确认要强制同步"' + tableName + '"表结构吗？').then(function() {
        return synchDb(tableName)
      }).then(() => {
        this.$modal.msgSuccess("同步成功")
      }).catch(() => {})
    },
    /** 打开导入表弹窗 */
    openImportTable() {
      this.$refs.import.show()
    },
    /** 打开创建表弹窗 */
    openCreateTable() {
      this.$refs.create.show()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.resetForm("queryForm")
      this.queryParams.pageNum = 1
      this.$refs.tables.sort(this.defaultSort.prop, this.defaultSort.order)
    },
    /** 预览按钮 */
    handlePreview(row) {
      previewTable(row.tableId).then(response => {
        this.preview.data = response.data
        this.preview.open = true
        this.preview.activeName = "domain.java"
      })
    },
    /** 高亮显示 */
    highlightedCode(code, key) {
      const vmName = key.substring(key.lastIndexOf("/") + 1, key.indexOf(".vm"))
      var language = vmName.substring(vmName.indexOf(".") + 1, vmName.length)
      const result = hljs.highlight(language, code || "", true)
      return result.value || '&nbsp;'
    },
    /** 复制代码成功 */
    clipboardSuccess() {
      this.$modal.msgSuccess("复制成功")
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tableId)
      this.tableNames = selection.map(item => item.tableName)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    /** 排序触发事件 */
    handleSortChange(column, prop, order) {
      this.queryParams.orderByColumn = column.prop
      this.queryParams.isAsc = column.order
      this.getList()
    },
    /** 修改按钮操作 */
    handleEditTable(row) {
      const tableId = row.tableId || this.ids[0]
      const tableName = row.tableName || this.tableNames[0]
      const params = { pageNum: this.queryParams.pageNum }
      this.$tab.openPage("修改[" + tableName + "]生成配置", '/tool/gen-edit/index/' + tableId, params)
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const tableIds = row.tableId || this.ids
      this.$modal.confirm('是否确认删除表编号为"' + tableIds + '"的数据项？').then(function() {
        return delTable(tableIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.gen-page {
  min-height: 100%;
  padding: 18px 20px 24px;
  background: #f3f5f7;
}

.gen-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 14px;
}

.gen-hero__title {
  margin: 0;
  color: #111827;
  font-size: 22px;
  font-weight: 760;
  line-height: 32px;
}

.gen-hero__desc {
  margin: 4px 0 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 20px;
}

.gen-hero__stats {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 0 0 auto;
}

.gen-stat-card {
  min-width: 118px;
  padding: 12px 16px;
  border: 1px solid #e6ebf2;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 8px 22px rgba(15, 23, 42, .04);
  text-align: right;

  span {
    display: block;
    color: #3f8f4d;
    font-size: 24px;
    font-weight: 760;
    line-height: 1;
  }

  label {
    display: block;
    margin-top: 6px;
    color: #6b7280;
    font-size: 12px;
    font-weight: 500;
  }
}

.gen-search-panel {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px 18px;
  margin-bottom: 12px;
  padding: 14px 16px 2px;
  border: 1px solid #e6ebf2;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, .035);
}

.gen-date-range {
  width: 260px;
}

::v-deep .gen-search-panel {
  .el-form-item {
    margin-right: 0;
    margin-bottom: 12px;
  }

  .el-form-item__label {
    color: #4b5563;
    font-weight: 600;
  }

  .el-input__inner {
    height: 34px;
    border-color: #dbe3ef;
    border-radius: 7px;
    background: #fbfcfe;
  }

  .el-range-editor.el-input__inner {
    display: inline-flex;
    align-items: center;
    padding: 0 10px;
  }

  .el-button {
    height: 34px;
    padding: 0 16px;
    border-radius: 7px;
    font-weight: 600;
  }
}

.gen-table-panel {
  overflow: hidden;
  border: 1px solid #e6ebf2;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, .035);
}

.gen-table-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 64px;
  padding: 14px 16px;
  border-bottom: 1px solid #edf1f7;

  h2 {
    margin: 0;
    color: #1f2937;
    font-size: 15px;
    font-weight: 720;
    line-height: 22px;
  }

  p {
    margin: 3px 0 0;
    color: #7b8794;
    font-size: 12px;
    line-height: 18px;
  }
}

.gen-table-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
  min-width: 0;

  .el-button + .el-button {
    margin-left: 0;
  }
}

.gen-table-name {
  color: #1f2937;
  font-weight: 650;
}

.gen-index {
  color: #6b7280;
  font-variant-numeric: tabular-nums;
}

.gen-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 210px;
  padding: 34px 16px;
}

.gen-empty__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-bottom: 12px;
  border-radius: 8px;
  color: #8aa0b8;
  background: #f4f7fb;
  font-size: 22px;
}

.gen-empty__title {
  color: #1f2937;
  font-size: 14px;
  font-weight: 650;
  line-height: 22px;
}

.gen-empty__desc {
  margin: 4px 0 14px;
  color: #7b8794;
  font-size: 12px;
  line-height: 20px;
}

::v-deep .gen-table-panel {
  .top-right-btn {
    margin-left: 4px;
  }

  .top-right-btn .el-button.is-circle {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }

  .el-button--mini {
    height: 32px;
    padding: 0 13px;
    border-radius: 7px;
    font-weight: 600;
  }

  .el-table {
    border: none;
    border-radius: 0;
    box-shadow: none;
  }

  .el-table .el-table__header-wrapper th {
    height: 46px;
    background: #f8fafc !important;
    color: #334155 !important;
    font-weight: 700;
  }

  .el-table td.el-table__cell {
    padding: 12px 0;
  }

  .el-table__row:hover > td.el-table__cell {
    background: #f8fbff !important;
  }

  .gen-table-ops .cell {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    gap: 8px;
  }

  .gen-table-ops .el-button--text {
    margin-left: 0;
    padding: 0;
    color: #2563eb;
    font-weight: 600;
  }

  .gen-table-ops .el-button--text.is-danger {
    color: #dc2626;
  }

  .pagination-container {
    margin-top: 0;
    padding: 12px 16px 14px;
    border-top: 1px solid #edf1f7;
  }
}

::v-deep .gen-preview-dialog {
  .el-dialog__body {
    background: #f3f4f6;
  }

  .hljs {
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    background: #ffffff;
    line-height: 1.7;
  }
}

@media (max-width: 768px) {
  .gen-page {
    padding: 14px;
  }

  .gen-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .gen-hero__stats {
    width: 100%;
  }

  .gen-stat-card {
    flex: 1;
    text-align: left;
  }

  .gen-search-panel {
    display: block;
    padding: 14px 14px 2px;
  }

  .gen-date-range {
    width: 100%;
  }

  .gen-table-panel__header {
    align-items: stretch;
    flex-direction: column;
  }

  .gen-table-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 480px) {
  .gen-hero__stats {
    flex-direction: column;
  }

  .gen-stat-card {
    width: 100%;
  }
}
</style>
