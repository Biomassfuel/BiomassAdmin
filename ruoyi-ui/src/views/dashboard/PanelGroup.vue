<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            访客
          </div>
          <count-to :start-val="0" :end-val="102400" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="message" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            消息
          </div>
          <count-to :start-val="0" :end-val="81212" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="money" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            金额
          </div>
          <count-to :start-val="0" :end-val="9280" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="shopping" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            订单
          </div>
          <count-to :start-val="0" :end-val="13600" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 16px;
  }

  .card-panel {
    position: relative;
    display: flex;
    align-items: center;
    min-height: 116px;
    padding: 18px;
    overflow: hidden;
    border: 1px solid #edf1f7;
    border-radius: 8px;
    background: #ffffff;
    color: #1d2129;
    box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
    cursor: pointer;
    transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease;

    &:hover {
      border-color: rgba(36, 104, 242, .28);
      box-shadow: 0 8px 24px rgba(29, 33, 41, 0.08);
      transform: translateY(-1px);
    }

    .icon-people {
      color: #2468f2;
      background: #edf4ff;
    }

    .icon-message {
      color: #00a870;
      background: #e8fff5;
    }

    .icon-money {
      color: #ff7d00;
      background: #fff7e8;
    }

    .icon-shopping {
      color: #7b61ff;
      background: #f2efff;
    }

    .card-panel-icon-wrapper {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      flex: 0 0 auto;
      width: 48px;
      height: 48px;
      margin: 0;
      border-radius: 8px;
      transition: color .18s ease, background .18s ease;
    }

    .card-panel-icon {
      width: 22px;
      height: 22px;
      font-size: 22px;
    }

    .card-panel-description {
      min-width: 0;
      margin: 0 0 0 16px;

      .card-panel-text {
        margin-bottom: 8px;
        color: #4e5969;
        font-size: 13px;
        font-weight: 600;
        line-height: 20px;
      }

      .card-panel-num {
        color: #1d2129;
        font-size: 24px;
        font-weight: 750;
        line-height: 30px;
      }
    }
  }
}

@media (max-width:550px) {
  .panel-group {
    margin-top: 12px;

    .card-panel {
      min-height: 106px;
      padding: 14px;
      flex-direction: column;
      align-items: flex-start;

      .card-panel-description {
        margin: 12px 0 0;
      }
    }
  }
}
</style>
