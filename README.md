# 澳琴空间预约系统

澳琴空间预约系统是基于 RuoYi 前后端分离版本扩展的空间预约、审核与资源管理后台，面向澳琴国际教育大学城的教室、会议室、公共空间等资源预约场景。

> 项目目录、Maven 坐标和部分包名仍沿用历史命名（如 `BiomassAdmin`、`com.ruoyi`），不影响系统按“澳琴空间预约系统”继续开发和部署。

## 项目定位

- 保留 RuoYi 用户、角色、菜单、部门、岗位、字典、参数、日志、定时任务、代码生成等后台基础能力。
- 扩展空间资源管理、预约申请、长周期预约、预约审核、取消审核、公开占用查询、统计看板等业务能力。
- 统一登录页、锁屏页、首页、侧边栏 Logo、代码生成页等界面风格。
- 新增邮箱验证码找回密码流程。
- 增强代码生成器，支持 `主子表（详情页）` 模板，适合主记录列表加独立详情页的业务场景。

## 技术栈

后端：

- Java 17
- Spring Boot 4
- Spring Security
- MyBatis
- PageHelper
- Redis
- JWT
- Druid
- Maven 多模块

前端：

- Vue 2.6
- Vue CLI 4
- Element UI 2
- Vue Router
- Vuex
- ECharts
- Sass

## 目录结构

```text
BiomassAdmin
├── ruoyi-admin       # 后端启动模块与 Controller
├── ruoyi-common      # 通用工具、常量、基础模型
├── ruoyi-framework   # 安全、认证、Redis、配置等框架能力
├── ruoyi-generator   # 代码生成模块与模板
├── ruoyi-quartz      # 定时任务模块
├── ruoyi-system      # 系统管理与空间预约业务领域层
├── ruoyi-ui          # Vue2 前端项目
├── sql               # 初始化、菜单、功能变更 SQL
└── doc               # 项目资料
```

## 核心功能

### 空间资源

- 楼栋、机构、空间类型、空间资源、设备、空间设备关系管理。
- 空间导入、导入批次记录、回收站、恢复和彻底删除。
- 预约时段、预约规则、不可预约时段配置。

### 预约业务

- 普通预约和长周期预约申请。
- 我的预约、全部预约、预约详情。
- 待审核预约、审核记录、审核日志。
- 预约取消申请、取消审核通过/驳回。
- 支持按主预约和预约明细两个粒度进行审核。

### 公开占用与统计

- 公开资源列表、公开预约汇总、公开预约明细。
- 空间占用查看和占用详情。
- 已审核预约空间列表。
- 统计看板、趋势、房间维度统计和导出。

### 账号与安全

- JWT 登录认证、验证码、登录失败锁定。
- Redis 缓存验证码、登录状态和密码找回验证码。
- 邮箱验证码找回密码，相关配置由 `sys_config` 管理。
- 菜单权限和接口权限继续使用 RuoYi 权限模型。

## 数据库脚本

`sql` 目录只保留完整初始化脚本。常见导入顺序：

1. `ry_20260417.sql`
2. `quartz.sql`
3. `space_reservation_20260609.sql`

其中 `ry_20260417.sql` 包含 RuoYi 基础表、默认数据、品牌文案和密码找回邮件配置；`quartz.sql` 包含 Quartz 调度表；`space_reservation_20260609.sql` 包含空间预约业务表、业务字典、菜单权限、统计菜单和预约状态刷新任务。

导入前请先确认目标数据库和字符集。全量脚本会重建相关表，已有业务数据请先备份。

## 本地运行

### 后端

1. 准备 MySQL 和 Redis。
2. 创建业务数据库，默认配置示例使用 `school`。
3. 按上方顺序导入 `sql` 目录下的完整初始化脚本。
4. 修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据源连接。
5. 修改 `ruoyi-admin/src/main/resources/application.yml` 中的 Redis、上传目录、Token 等配置。
6. 启动 `ruoyi-admin` 模块中的 `com.ruoyi.RuoYiApplication`。

构建命令：

```bash
mvn -DskipTests package
```

### 前端

```bash
cd ruoyi-ui
npm install --legacy-peer-deps
npm run dev
```

默认开发地址：

```text
http://localhost:80
```

前端开发代理默认转发到：

```text
http://localhost:8080
```

生产构建：

```bash
cd ruoyi-ui
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:prod
```

## 关键配置

- 后端服务端口：`ruoyi-admin/src/main/resources/application.yml` 中的 `server.port`，默认 `8080`。
- 数据源：`ruoyi-admin/src/main/resources/application-druid.yml`。
- Redis：`ruoyi-admin/src/main/resources/application.yml` 中的 `spring.data.redis`。
- 文件上传目录：`ruoyi.profile`。
- 前端标题和接口前缀：`ruoyi-ui/.env.*`。
- 前端代理目标：`ruoyi-ui/vue.config.js` 中的 `baseUrl`。
- 密码找回邮件参数：系统参数表中的 `sys.mail.smtp.*` 与 `sys.password.reset.*`。

## 代码生成增强

项目新增 `主子表（详情页）` 代码生成模板，位于：

```text
ruoyi-generator/src/main/resources/vm/vue/sub-detail
```

模板特点：

- 面向 `Vue2 + Element UI`。
- 主表保留列表查询、新增、修改、删除、导出。
- 主表列表增加 `详情` 操作。
- 详情页为独立页面，不使用弹窗承载。
- 详情页上方展示主表信息，下方展示子表列表。
- 子表支持新增、修改、删除，新增/修改通过弹窗即时提交。
- 菜单 SQL 会额外生成隐藏详情页路由。

使用流程：

1. 准备主表和子表，子表需要通过外键关联主表主键。
2. 在代码生成页面导入主表和子表。
3. 编辑主表配置，选择生成模板 `主子表（详情页）`。
4. 前端类型保持 `Vue2 Element UI`。
5. 选择关联子表和子表外键字段。
6. 检查字段的查询、列表、表单、必填和字典配置。
7. 生成代码并复制到对应目录。
8. 导入生成的菜单 SQL。
9. 重启后端并重新登录前端，让动态菜单路由重新加载。

## 验证

后端构建：

```bash
mvn -DskipTests package
```

前端构建：

```bash
cd ruoyi-ui
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:prod
```

## 开发注意

- 本项目不是原始 RuoYi 模板仓库，而是在 RuoYi 能力之上叠加了空间预约业务。
- `SpaceBook-statistics-worktree` 是同级目录中的独立工作树，请在对应分支内单独处理。
- `node_modules`、`target`、`.git` 不属于业务源码，检查或搜索项目时应排除。
- 修改菜单、权限或系统参数后，建议重新登录前端，以刷新动态路由和权限缓存。
