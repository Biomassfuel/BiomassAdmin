# BiomassAdmin

BiomassAdmin 是一个基于 RuoYi 前后端分离版本整理出来的后台管理模板。项目目标是让默认若依界面更清爽、更适合作为二次开发底座，同时删除或弱化一些当前用不上的模块入口。

> `BiomassAdmin` 只是历史命名。本项目没有生物质相关业务逻辑，也不是生物质行业系统。

## 项目定位

- 基于 RuoYi 3.9.2 做界面重构和结构整理。
- 保留用户、角色、菜单、部门、岗位、字典、参数、日志、代码生成等后台核心能力。
- 优化登录页、锁屏页、侧边栏 Logo、代码生成页面等视觉体验。
- 新增更适合业务详情场景的代码生成模板：`主子表（详情页）`。
- 继续使用原若依技术栈，方便从 RuoYi 项目迁移或继续扩展。

## 技术栈

后端：

- Java 17
- Spring Boot
- Spring Security
- MyBatis
- Redis
- JWT
- Maven 多模块

前端：

- Vue 2
- Vue CLI
- Element UI
- Vuex
- Vue Router
- Sass

## 目录结构

```text
BiomassAdmin
├── ruoyi-admin       # 后端启动模块
├── ruoyi-common      # 通用工具模块
├── ruoyi-framework   # 框架核心模块
├── ruoyi-generator   # 代码生成模块
├── ruoyi-quartz      # 定时任务模块
├── ruoyi-system      # 系统管理模块
├── ruoyi-ui          # 前端项目
├── sql               # 数据库脚本和测试 SQL
└── doc               # 项目文档
```

## 代码生成增强

项目新增了一个代码生成模板：

```text
主子表（详情页）
```

它不会替换若依原有的 `主子表（增删改查）` 模板。两套模板并行存在。

### 模板特点

- 仅支持 `Vue2 Element UI`。
- 主表列表页保留查询、新增、修改、删除、导出。
- 主表列表页新增 `详情` 操作。
- 详情页是独立页面，不是弹窗。
- 详情页上方展示主表信息。
- 详情页下方展示子表列表。
- 子表支持新增、修改、删除。
- 子表新增/修改使用弹窗即时提交。
- 主表新增/修改不再携带子表表单列表。
- 主表删除时仍会删除关联子表，避免孤儿数据。
- 菜单 SQL 会额外生成隐藏详情页路由。

### 使用流程

1. 准备主表和子表，子表需要有明确外键指向主表主键。
2. 在代码生成页面导入主表和子表。
3. 编辑主表配置。
4. 选择生成模板：`主子表（详情页）`。
5. 前端类型保持：`Vue2 Element UI`。
6. 选择关联子表和子表外键字段。
7. 检查主表、子表字段的查询、列表、表单、必填和字典配置。
8. 生成代码并复制到对应目录。
9. 导入生成的菜单 SQL。
10. 重启后端并重新登录前端，让动态菜单路由重新加载。

测试用主子表 SQL：

```text
sql/demo_purchase_master_sub.sql
```

## 本地运行

### 后端

1. 创建数据库。
2. 导入 `sql` 目录下的初始化脚本。
3. 修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库连接。
4. 启动 `ruoyi-admin` 模块。

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

生产构建：

```bash
cd ruoyi-ui
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:prod
```

## 已调整内容

- 重做登录页视觉风格。
- 重做锁屏页视觉风格。
- 优化侧边栏 Logo 展示。
- 重新设计代码生成页面布局。
- 新增 `主子表（详情页）` 代码生成模板。
- 为详情页路由高亮增加轻量处理。
- 增加主子表详情页测试 SQL。

## 验证

当前主要验证命令：

```bash
mvn -DskipTests package
```

```bash
cd ruoyi-ui
$env:NODE_OPTIONS="--openssl-legacy-provider"
npm run build:prod
```

## 说明

本项目适合作为 RuoYi 的美化精简版模板继续开发。它不是完整行业业务系统，也不绑定生物质、能源或物料管理场景。

原始项目请参考 RuoYi 官方仓库与文档。
