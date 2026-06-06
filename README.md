# BiomassAdmin

BiomassAdmin 是基于 Vue2、Element UI、Spring Boot、Spring Security、Redis、JWT 的前后端分离后台管理系统。

## 改造目标

- 保留后台管理基础能力和核心 RBAC 权限体系。
- 保留用户、角色、菜单、部门、岗位、字典、参数、日志、个人中心和代码生成。
- 删除默认官网入口、注册入口、测试接口、在线表单构造器、Swagger/Druid 生产菜单等冗余模块。
- 将整体界面改造成现代化 SaaS 控制台风格：极简、卡片化、圆角、轻阴影、清晰间距、蓝白灰配色。
- 保持 Vue2、Element UI、Vuex、Vue Router 技术栈不变。

## 核心能力

- 登录认证、验证码、动态路由、权限指令。
- 用户管理、角色管理、菜单管理、部门管理、岗位管理。
- 字典管理、参数管理、通知公告、操作日志、登录日志。
- 在线用户、服务监控、缓存监控、定时任务。
- 代码生成：保留 `ruoyi-generator` 模块，生成的 Vue2 页面已统一为新版 SaaS 风格。

## 前端开发

```bash
cd ruoyi-ui
npm install --legacy-peer-deps
npm run dev
```

生产构建：

```bash
cd ruoyi-ui
$env:NODE_OPTIONS='--openssl-legacy-provider'
npm run build:prod
```

## 后端构建

```bash
mvn -DskipTests package
```

## 说明

当前项目仍保留 RuoYi 的成熟后台基础能力与接口兼容性，但默认产品呈现已调整为 `BiomassAdmin`。
