# JobHub 前端项目

基于 Vue 2 的求职社区平台前端项目，根据原型设计和后端 API 文档开发。

## 项目结构

```
fx/
├── src/
│   ├── api/              # API 服务
│   │   ├── services/     # 各模块 API 服务
│   │   ├── mock/         # Mock 数据
│   │   └── http.js       # HTTP 请求封装
│   ├── components/       # 公共组件
│   ├── views/            # 页面组件
│   │   ├── auth/         # 认证相关页面
│   │   ├── Home.vue      # 首页
│   │   ├── Topics.vue    # 话题广场
│   │   ├── TopicDetail.vue # 话题详情
│   │   ├── Chatroom.vue  # 聊天室
│   │   ├── CircleDetail.vue # 小组详情
│   │   ├── Profile.vue   # 个人主页
│   │   ├── ProfileEdit.vue # 编辑个人信息
│   │   └── CreatorDashboard.vue # 专家后台
│   ├── router/           # 路由配置
│   └── store/            # Vuex 状态管理
├── public/               # 静态资源
└── package.json          # 项目配置
```

## 功能模块

### 1. 认证模块
- **登录页面** (`/auth/login`)
- **注册页面** (`/auth/register`) - 支持身份标签、情感标签、个性签名
- **忘记密码** (`/auth/forgot`)
- **身份选择** (`/auth/select-identity`)

### 2. 首页
- 身份切换功能
- 热门直播与活动轮播
- 基于身份的推荐内容

### 3. 话题广场
- 话题列表展示
- 标签筛选
- 排序功能（热度/最新）

### 4. 话题详情
- 话题信息展示
- 发布问题/经验
- 互动内容（评论、引用回复、点赞、收藏）
- 聊天室预告

### 5. 聊天室
- 实时聊天功能
- 聊天室信息展示
- 管理员工具（仅管理员可见）

### 6. 兴趣圈子
- 小组列表
- 小组详情
- 小组动态
- 资源库
- 成员管理

### 7. 个人主页
- 基础信息展示
- 数据管理：
  - 浏览历史（支持删除、批量清除）
  - 我的圈子（支持进入、退出）
  - 我发布的（支持编辑、删除、置顶）
  - 我的收藏（支持收藏夹管理）
- 隐私设置

### 8. 专业中心
- 专家后台
- 数据面板
- 内容管理

## API 配置

### 后端 API 地址

默认后端地址：`http://localhost:8080`

可以通过环境变量 `VUE_APP_API_BASE` 配置：

```bash
# .env.development
VUE_APP_API_BASE=http://localhost:8080
VUE_APP_USE_MOCK=true
```

### API 接口

根据后端文档，主要接口包括：

- **注册**: `POST /api/user/register`
  - 参数：`username`, `password`, `identityTag`, `emotionTag`, `signature`
  - Header: `Authorization` (可选)

- **登录**: `POST /v1/auth/login`
- **忘记密码**: `POST /v1/auth/forgot-password/send-code`
- **重置密码**: `PUT /v1/auth/reset-password`
- **选择身份**: `PUT /v1/auth/select-identity`

### Mock 模式

项目默认开启 Mock 模式，可以通过环境变量关闭：

```bash
VUE_APP_USE_MOCK=false
```

## 开发

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run serve
```

### 构建生产版本

```bash
npm run build
```

## 技术栈

- Vue 2.6.14
- Vue Router 3.6.5
- Vuex 3.6.2
- Axios 1.7.7

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 推荐内容和热门活动 |
| `/auth/login` | 登录 | 用户登录 |
| `/auth/register` | 注册 | 用户注册 |
| `/auth/forgot` | 忘记密码 | 密码重置 |
| `/auth/select-identity` | 身份选择 | 选择用户身份 |
| `/topics` | 话题广场 | 话题列表 |
| `/topic/:id` | 话题详情 | 话题详情页 |
| `/circles` | 兴趣圈子 | 小组列表 |
| `/circle/:id` | 小组详情 | 小组详情页 |
| `/chatroom/:id` | 聊天室 | 聊天室页面 |
| `/profile/me` | 个人主页 | 个人主页 |
| `/profile/edit` | 编辑信息 | 编辑个人信息 |
| `/profile/settings` | 隐私设置 | 隐私设置 |
| `/pro` | 专业中心 | 专业中心首页 |
| `/creator` | 专家后台 | 创作者后台 |
| `/messages` | 消息中心 | 消息通知 |

## 注意事项

1. **API 地址配置**：确保后端 API 地址正确配置
2. **Mock 模式**：开发时默认使用 Mock 数据，生产环境需要关闭
3. **身份认证**：登录后 token 存储在 `localStorage` 中，key 为 `jobhub_token`
4. **路由守卫**：除认证相关页面外，其他页面需要登录才能访问

## 开发规范

1. 组件命名使用 PascalCase
2. 页面组件放在 `views/` 目录
3. 公共组件放在 `components/` 目录
4. API 服务统一放在 `api/services/` 目录
5. 样式使用 scoped，避免全局污染

## 后续优化

- [ ] 接入真实后端 API
- [ ] 完善错误处理
- [ ] 添加单元测试
- [ ] 性能优化
- [ ] 响应式设计优化

