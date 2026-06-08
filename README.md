# BiomassAdmin

BiomassAdmin 是一个基于 RuoYi 前后端分离版本改造的后台管理模板项目。这个仓库的目标很简单：让默认若依界面更现代、更清爽，并删除一些当前用不上的模块和入口，保留后台管理系统的核心能力。

> 项目名称中的 `Biomass` 只是历史命名，不代表业务方向。本项目没有任何生物质相关业务逻辑，也不是生物质行业系统。

## 项目定位

- 基于 RuoYi 3.9.2 做二次整理和界面重构。
- 保留用户、角色、菜单、部门、岗位、字典、参数、日志、代码生成等常用后台能力。
- 优化登录页、锁屏页、侧边栏 Logo、代码生成等页面的视觉体验。
- 删除或弱化不需要的默认入口和冗余模块，让项目更适合作为简洁后台模板继续开发。
- 不改变主要技术栈，仍然使用 Spring Boot、Spring Security、MyBatis、Vue 2、Vuex、Vue Router 和 Element UI。

## 技术栈

后端：

- Java 17
- Spring Boot 4
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
├── sql               # 数据库脚本
└── doc               # 项目文档
```

## 本地运行

### 后端

1. 创建数据库并导入 `sql` 目录下的初始化脚本。
2. 修改后端配置中的数据库、Redis 等连接信息。
3. 启动 `ruoyi-admin` 模块。

也可以使用 Maven 构建：

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

- 重做登录页视觉风格，去掉默认深色大背景。
- 重做锁屏页视觉风格，与新的轻量后台风格保持一致。
- 替换侧边栏和页面中的默认 Logo 展示。
- 重新设计代码生成页面布局，让筛选、操作栏和表格更规整。
- 保留若依核心后台能力，减少不必要的默认展示和业务暗示。

## 说明

本项目适合作为 RuoYi 的美化精简版模板继续开发。它不是一个完整行业业务系统，也不绑定任何生物质、能源或物料管理场景。

原始项目请参考 RuoYi 官方仓库与文档。
