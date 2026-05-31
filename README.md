# BlogHub — 全栈博客平台

基于 **Vue 3 + Spring Boot** 的现代化全栈博客系统，支持文章管理、评论互动、用户中心和后台管理。

---

## 技术栈

| 层级 | 技术 |
|:----|:-----|
| **前端** | Vue 3 + Vite 5 + Element Plus 2 + Pinia + Vue Router 4 |
| **后端** | Spring Boot 2.7 + MyBatis-Plus 3.5 + JWT (jjwt) |
| **数据库** | MySQL 8+ (utf8mb4) |
| **认证** | JWT 双 Token（Access 24h + Refresh 7d）+ BCrypt |
| **构建** | Maven + npm |
| **容器化** | Docker (多阶段构建) |
| **其他** | ECharts 6、Axios、markdown-it、jsoup |

---

## 快速开始

### 前置条件

- JDK 8+
- Maven 3.x
- Node.js 18+
- MySQL 8+

### 1. 初始化数据库

```bash
mysql -u root -p --default-character-set=utf8 < backend/sql/init.sql
```

### 2. 启动后端

```bash
mvn -f backend/pom.xml spring-boot:run
```

后端启动在 `http://localhost:8080`，API 文档访问 `http://localhost:8080/swagger-ui.html`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:3000`

### 4. 登录测试

默认管理员账号：**admin** / **admin123**

---

## 功能概览

### 📝 文章管理
- Markdown 双栏编辑器 + 实时预览
- 文章分类/标签/搜索/归档
- 阅读量统计、编辑时间追踪

### 🔐 用户系统
- 注册/登录（JWT 双 Token，自动刷新）
- 个人中心（昵称/邮箱/简介编辑）
- 我的文章/收藏夹

### 💬 互动功能
- 点赞（基于访客 ID，无需登录）
- 收藏（需登录）
- 评论（嵌套回复 + XSS 防护）

### 🛠️ 管理后台（Admin）
- 仪表盘（文章/用户/评论统计）
- 用户管理（禁用/启用）
- 评论管理（审核）
- 操作日志

### 🎨 前端特性
- 响应式侧边栏布局
- 骨架屏加载动画
- 阅读进度条
- 标签云（大小随热度变化）
- 全屏 Markdown 编辑器

### 🖼️ 文件上传
- 拖拽/点击上传
- 类型/大小校验
- UUID 重命名，按日期分目录
- 编辑器内一键插入图片

---

## 项目结构

```
BlogHub-pro/
├── backend/                     # Spring Boot 后端
│   ├── pom.xml
│   ├── Dockerfile
│   ├── sql/
│   │   ├── init.sql             # 建库建表 + 默认数据
│   │   ├── migration-like.sql
│   │   └── migration-bookmark.sql
│   └── src/main/
│       ├── java/com/bloghub/
│       │   ├── aspect/          # AOP 切面（日志/限流）
│       │   ├── common/          # 公共类（Result/BaseEntity/枚举）
│       │   ├── config/          # 配置（JWT/CORS/MyBatis-Plus）
│       │   ├── controller/      # 控制器（15个）
│       │   ├── dto/             # 数据传输对象
│       │   ├── entity/          # 数据库实体（11个）
│       │   ├── exception/       # 全局异常处理
│       │   ├── mapper/          # MyBatis-Plus Mapper
│       │   └── service/         # 业务逻辑层
│       └── resources/
│           ├── application.yml
│           └── mapper/          # MyBatis XML 映射
├── frontend/                    # Vue 3 前端
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                 # API 调用层
│       ├── components/          # 公共组件
│       ├── composables/         # 可复用逻辑
│       ├── layouts/             # 页面布局
│       ├── router/              # 路由配置
│       ├── stores/              # Pinia 状态管理
│       ├── styles/              # 全局样式
│       ├── utils/               # 工具函数
│       └── views/               # 页面视图（22个）
├── PROJECT_front.md             # 前端规范文档
└── project-backend.md           # 后端规范文档
```

## API 概览

| 方法 | 路径 | 说明 | 权限 |
|:----|:-----|:-----|:----:|
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录 | 公开 |
| GET | `/api/posts` | 文章列表 | 公开 |
| GET | `/api/posts/{slug}` | 文章详情 | 公开 |
| POST | `/api/posts` | 创建文章 | 登录 |
| PUT | `/api/posts/{slug}` | 编辑文章 | 登录 |
| DELETE | `/api/posts/{id}` | 删除文章 | 登录 |
| POST | `/api/posts/{slug}/like` | 点赞切换 | 公开 |
| POST | `/api/posts/{slug}/comments` | 发表评论 | 公开 |
| POST | `/api/upload` | 上传图片 | 公开 |
| GET | `/api/admin/stats/dashboard` | 仪表盘数据 | Admin |

完整 API 文档在 Swagger UI：`http://localhost:8080/swagger-ui.html`

---

## License

MIT
