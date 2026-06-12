# 澳琴空间预约系统 UI

这是澳琴空间预约系统的 Vue2 + Element UI 前端项目，负责登录、首页、空间资源管理、预约申请、审核、公开占用查询、统计看板和系统管理等后台页面。

## 技术栈

- Vue 2.6
- Vue CLI 4
- Element UI 2.15
- Vue Router 3
- Vuex 3
- Axios
- ECharts
- Sass

## 目录说明

```text
ruoyi-ui
├── public              # HTML 模板与静态资源
├── src/api             # 后端接口封装
├── src/assets          # 样式、图标、图片
├── src/components      # 通用组件
├── src/layout          # 后台布局
├── src/router          # 静态路由与动态路由入口
├── src/store           # Vuex 状态管理
├── src/utils           # 请求、权限、校验、密码规则等工具
└── src/views           # 页面
```

空间预约相关页面主要在：

```text
src/views/space
├── audit               # 待审核、取消审核、审核记录、审核日志
├── components          # 空间信息卡片、周占用表等复用组件
├── reservation         # 预约申请、长周期预约、我的预约、全部预约、公开占用
├── room                # 空间、空间类型、设备
└── statistics          # 统计看板
```

## 开发运行

安装依赖：

```bash
npm install --legacy-peer-deps
```

启动开发服务：

```bash
npm run dev
```

默认访问地址：

```text
http://localhost:80
```

开发环境接口前缀为 `/dev-api`，代理目标在 `vue.config.js` 中配置，默认：

```text
http://localhost:8080
```

## 构建

生产构建：

```bash
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:prod
```

预发布构建：

```bash
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:stage
```

Node 17 及以上运行 Vue CLI 4 时建议保留 `NODE_OPTIONS=--openssl-legacy-provider`。

## 常用配置

- 页面标题：`.env.development`、`.env.production`、`.env.staging` 中的 `VUE_APP_TITLE`。
- 接口前缀：`.env.*` 中的 `VUE_APP_BASE_API`。
- 开发服务端口：`vue.config.js` 中的 `port`，默认 `80`。
- 后端代理目标：`vue.config.js` 中的 `baseUrl`。
- 路由模式：`src/router/index.js` 使用 `history`。
- 登录和找回密码接口：`src/api/login.js`。
- 空间预约接口：`src/api/space`。

## 页面能力

- 登录、验证码、锁屏、个人中心。
- 忘记密码，支持邮箱验证码重置。
- 空间资源管理：空间、类型、设备、导入、回收站。
- 预约管理：预约申请、长周期预约、我的预约、全部预约、详情。
- 审核管理：待审核、取消审核、审核记录、审核日志。
- 公开查询：公开空间、公开预约汇总、公开预约明细、占用详情。
- 统计看板：概览指标、趋势图、房间统计和导出。
- 系统管理：用户、角色、菜单、部门、岗位、字典、参数、通知等。

## 开发注意

- 权限控制沿用 RuoYi：页面按钮用 `v-hasPermi`，接口权限由后端 `@PreAuthorize` 校验。
- 动态菜单来自后端，新增菜单或权限后需要重新登录刷新路由。
- 新页面应放在 `src/views` 下，并通过菜单 SQL 或动态路由接入。
- 请求封装统一使用 `src/utils/request.js`。
- 密码规则提示和校验逻辑集中在 `src/utils/passwordRule.js`。
