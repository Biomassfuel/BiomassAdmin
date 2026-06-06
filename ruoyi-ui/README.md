# Biomass Admin UI

Vue2 + Element UI 前端控制台，已统一为现代 SaaS 后台风格。

## 开发

```bash
npm install --legacy-peer-deps
npm run dev
```

默认访问：

```text
http://localhost:80
```

## 构建

```bash
$env:NODE_OPTIONS='--openssl-legacy-provider'
npm run build:prod
```

Node 18 以上运行 Vue CLI 4 时建议保留 `NODE_OPTIONS` 设置。
