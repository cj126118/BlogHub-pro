# 项目技术总结文档

> **项目名称：** BlogHub（博客后端）
> **用途：** 供 AI 编码助手理解项目风格，在新项目中复用同样的架构范式和最佳实践。

---

## 一、技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **语言** | Java |  1.8（编译目标） | |
| **框架** | Spring Boot | 2.7.18 | 核心 Web 框架 |
| **ORM** | MyBatis-Plus | 3.5.5 | CRUD 增强 + 分页 + 自动字段填充 |
| **数据库** | MySQL 8+ | utf8mb4/utf8mb4_unicode_ci | |
| **连接池** | HikariCP | 内置于 Boot 2.7 | 最小 5，最大 20 连接 |
| **认证与授权** | JWT (jjwt 0.11.5) + BCrypt | 双 Token 模式 | Access 24h / Refresh 7d |
| **API 文档** | SpringDoc OpenAPI 1.7 | Swagger UI | prod 环境关闭 |
| **构建** | Maven | Wrapper 或系统 Maven 3.x | |
| **容器化** | Docker 多阶段构建 | `maven:3.9-eclipse-temurin-17` → `eclipse-temurin:17-jre` | |
| **AOP 切面** | Spring AOP | — | 日志/限流/审计三个切面 |
| **参数校验** | Hibernate Validator (`@Valid`) | — | |
| **XSS 防护** | jsoup 1.17.2 | — | 评论内容 HTML 清洗 |
| **日志** | SLF4J + Logback | 自定义 `logback-spring.xml` | |
| **JSON** | Jackson (Boot 自动配置) | 日期格式 `yyyy-MM-dd HH:mm:ss` | |

---

## 二、项目结构

```
project-root/
├── pom.xml                          # Maven 依赖 + 构建配置
├── Dockerfile                       # 多阶段 Docker 构建
├── sql/
│   └── init.sql                     # 数据库初始化脚本（建库 + 核心表）
├── src/main/
│   ├── java/com/blog/
│   │   ├── BlogApplication.java     # 启动类 (@SpringBootApplication + @EnableScheduling)
│   │   ├── aspect/
│   │   │   └── LoggingAspect.java   # Service 层方法日志（入参/返回值/耗时）
│   │   ├── common/
│   │   │   ├── AuditLog.java        # 审计日志注解
│   │   │   ├── AuditLogAspect.java  # 审计日志切面实现
│   │   │   ├── BaseEntity.java      # 实体基类（id, createdAt, updatedAt）
│   │   │   ├── enums/*.java         # 枚举（ErrorCode, PostStatus, UserRole）
│   │   │   ├── HtmlSanitizer.java   # HTML 标签清洗工具
│   │   │   ├── PageParam.java       # 分页请求参数封装
│   │   │   ├── PageResult.java      # 分页响应封装
│   │   │   ├── RateLimit.java       # 限流注解
│   │   │   ├── RateLimitAspect.java # 限流切面（本地计数器）
│   │   │   ├── Result.java          # 统一 API 响应体
│   │   │   ├── ResultCode.java      # 统一响应码枚举
│   │   │   └── VisitorIdUtil.java   # 访客标识工具
│   │   ├── config/
│   │   │   ├── AuthInterceptor.java # JWT 认证拦截器
│   │   │   ├── CorsConfig.java      # CORS 跨域配置
│   │   │   ├── JwtUtil.java         # JWT 生成/解析/校验工具
│   │   │   ├── MyBatisPlusConfig.java # MyBatis-Plus 分页插件
│   │   │   ├── MyMetaObjectHandler.java # 自动填充 createdAt/updatedAt
│   │   │   ├── RestTemplateConfig.java   # HTTP 客户端
│   │   │   ├── SecurityConfig.java  # BCrypt 密码编码器
│   │   │   ├── UploadHeaderFilter.java   # 上传请求头处理
│   │   │   └── WebMvcConfig.java    # 拦截器注册 + 静态资源映射
│   │   ├── controller/              # 18 个控制器
│   │   ├── dto/                     # 14 个数据传输对象
│   │   ├── entity/                  # 17 个数据库实体
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── GlobalExceptionHandler.java # 全局异常处理 (@RestControllerAdvice)
│   │   ├── mapper/                  # 17 个 MyBatis-Plus Mapper
│   │   └── service/
│   │       ├── impl/                # 服务实现（9 个 ServiceImpl）
│   │       └── *.java               # 服务接口（9 个 Service）
│   └── resources/
│       ├── application.yml          # 主配置文件 + prod profile
│       ├── db/
│       │   ├── migration.sql        # 基础迁移（日志/版本/系列/关注/通知）
│       │   ├── migration-v2.sql     # 置顶 + 每日阅读量
│       │   ├── migration-tag-system.sql # 标签系统升级（含存量迁移存储过程）
│       │   ├── migration-album.sql  # 宝宝相册
│       │   ├── migration-hot-topic.sql # 每日热点
│       │   ├── migration-images.sql # 上传图片记录
│       │   └── migration-comment-userid.sql # 评论表追加 user_id
│       └── logback-spring.xml       # 日志配置
```

---

## 三、架构分层

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Controller 层                                 │
│    @RestController + @RequestMapping("/api/...")                     │
│    职责：参数解析、权限校验前置、调用 Service、返回 Result<T>           │
│    注解：@Valid 参数校验、@RateLimit 限流、@AuditLog 审计、@Operation  │
├──────────────────────────────────────────────────────────────────────┤
│                        Web 配置层                                     │
│    AuthInterceptor  — JWT 拦截校验（白名单排除）                        │
│    CorsConfig       — CORS 跨域过滤器                                  │
│    WebMvcConfig     — 注册拦截器 + 静态资源映射                         │
├──────────────────────────────────────────────────────────────────────┤
│                        Service 层                                     │
│    Interface + Impl 模式                                              │
│    职责：业务逻辑编排、事务管理 @Transactional、ORM 调用                │
│    注解：@Service + @Transactional(rollbackFor = Exception.class)     │
├──────────────────────────────────────────────────────────────────────┤
│                        Mapper 层                                      │
│    extends BaseMapper<T> — 零 XML 基础 CRUD                           │
│    LambdaQueryWrapper / LambdaUpdateWrapper — 类型安全条件构建         │
│    IPage<T> + Page<T> — 分页查询                                     │
├──────────────────────────────────────────────────────────────────────┤
│                        Entity 层                                      │
│    继承 BaseEntity（id, createdAt, updatedAt）                         │
│    @TableName + @TableField + @TableId                                │
│    Meta-Object Handler 自动填充时间字段                                │
├──────────────────────────────────────────────────────────────────────┤
│                        基础设施层                                      │
│    Result<T>        — 统一 JSON 响应体                                │
│    GlobalExceptionHandler — 统一异常处理（@RestControllerAdvice）      │
│    BusinessException — 业务异常（可指定 code + message）               │
│    ResourceNotFoundException — 资源不存在异常                          │
│    PageResult       — 分页响应封装                                    │
│    JwtUtil          — Token 工具                                      │
│    HtmlSanitizer    — XSS 防护                                        │
│    VisitorIdUtil    — 匿名/登录访客标识统一                            │
└──────────────────────────────────────────────────────────────────────┘
```

---

## 四、请求生命周期

```
┌─────────────────────────────────────────────────────────────────────────┐
│  1. 客户端 HTTP 请求 → http://host:8080/api/posts                      │
├─────────────────────────────────────────────────────────────────────────┤
│  2. CorsFilter（Spring Boot 过滤器链）                                  │
│     - 放行 /api/** 和 /uploads/**                                       │
│     - 设置 AllowOrigin / AllowCredentials / AllowHeaders                │
├─────────────────────────────────────────────────────────────────────────┤
│  3. AuthInterceptor（HandlerInterceptor：在 Controller 之前）           │
│     - OPTIONS 请求直接放行                                               │
│     - 检查路径是否在 excludePathPatterns 中：                            │
│       /api/auth/** , /api/posts , /api/posts/** , /api/users/*/profile  │
│       /api/stats/** , /api/upload/** , /api/hot-topics/** , /uploads/** │
│     - 白名单 → 直接放行                                                  │
│     - 非白名单 → 校验 Authorization: Bearer <token>                     │
│       · 无 token / token 过期 → 返回 401 统一错误                       │
│       · 校验通过 → request.setAttribute("userId"/"username"/"role")      │
├─────────────────────────────────────────────────────────────────────────┤
│  4. Controller 方法                                                    │
│     - @Valid 校验请求体（失败走 MethodArgumentNotValidException）        │
│     - @PathVariable / @RequestParam / @RequestAttribute 参数绑定        │
│     - 调用 Service 层                                                   │
├─────────────────────────────────────────────────────────────────────────┤
│  5. AOP 切面（环绕 Controller → Service）                               │
│     - @RateLimit：本地计数器窗口限流，超限抛 BusinessException(429)      │
│     - @AuditLog：记录操作日志到 sys_log 表                               │
│     - LoggingAspect：记录 Service 方法入参、返回值、执行耗时(>200ms警告) │
├─────────────────────────────────────────────────────────────────────────┤
│  6. Service 层                                                         │
│     - 业务逻辑编排                                                     │
│     - @Transactional 事务管理                                           │
│     - 调用 Mapper（MyBatis-Plus）                                       │
├─────────────────────────────────────────────────────────────────────────┤
│  7. MyBatis-Plus Mapper                                                │
│     - MyMetaObjectHandler 自动填充 createdAt / updatedAt                 │
│     - MyBatisPlusInterceptor 分页插件                                   │
│     - SQL 执行 → MySQL                                                 │
├─────────────────────────────────────────────────────────────────────────┤
│  8. 返回链路                                                           │
│     Service → Controller → Result.success(data)                        │
│     ↓                                                                  │
│     GlobalExceptionHandler（如未捕获异常 → 500 兜底）                    │
│     ↓                                                                  │
│     JSON 响应 → 客户端                                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 五、架构亮点与设计风格

### 5.1 RESTful 资源化设计

- 路径遵循 `/api/{资源}` + HTTP 动词语义化
  - `GET /api/posts` = 列表查询
  - `POST /api/posts` = 创建
  - `PUT /api/posts/{id}` = 全量更新
  - `DELETE /api/posts/{id}` = 删除
- 统一响应体 `Result<T>`，所有接口返回标准格式：
  ```json
  { "code": 200, "message": "success", "data": { ... } }
  ```
- 统一状态码枚举 `ResultCode`：SUCCESS(200), CREATED(201), NO_CONTENT(204), BAD_REQUEST(400), UNAUTHORIZED(401), NOT_FOUND(404), VALID_ERROR(422), INTERNAL_ERROR(500)

### 5.2 JWT 无状态双 Token 认证

- **Access Token**（24h 过期）—— 请求认证用
- **Refresh Token**（7d 过期）—— 换取新 Access Token
- 拦截器仅校验非白名单路径，白名单路径完全放行（文章列表/详情、登录、注册等）
- 匿名访客友好：点赞/收藏/评论均不强制登录，通过 `visitorId`（已登录 = `user:{id}`，未登录 = `visitor:{ip}`）区分用户

### 5.3 分层 AOP 切面

- **LoggingAspect** — 切入所有 Service 方法，自动记录入参、返回值、异常，执行耗时超过 200ms 打印警告
- **RateLimitAspect** — 基于 `@RateLimit(key, max, period)` 注解的本地计数器限流，应用于登录（5次/分钟）和注册（3次/分钟）等敏感接口
- **AuditLogAspect** — 基于 `@AuditLog(action, resource)` 注解记录用户操作到 `sys_log` 表，含 userId、IP、操作描述

### 5.4 统一异常处理范式

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 业务异常 → code 可自定义
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handle(BusinessException e) { ... }

    // 校验失败（@Valid）→ 422 + 拼接所有字段错误信息
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handle(MethodArgumentNotValidException e) { ... }

    // 参数缺失/类型错误 → 400
    // 请求体格式错误 → 400
    // 兜底 → 500（日志打印完整 stack trace，响应不暴露）
}
```

### 5.5 访客与登录用户统一

- 未登录用户通过 `VisitorIdUtil` 生成基于 IP 的访客标识
- 已登录用户使用 `user:{userId}` 作为访客标识
- 点赞/收藏/评论等交互，同一访客标识保证幂等性

### 5.6 安全的文件上传

- 按日期分目录：`uploads/2025/03/uuid.png`
- 白名单文件类型：jpg/png/gif/webp/svg，5MB 上限
- UUID 重命名防冲突/恶意文件名
- 路径遍历防护：`Path.normalize()` + 确保在 uploadDir 内
- 图片记录写入 `images` 表，支持关联到文章

### 5.7 增量数据库迁移

- 所有 DDL 用 `ALTER TABLE ADD COLUMN IF NOT EXISTS`，幂等可重跑
- 迁移脚本按功能拆分（`migration-tag-system.sql`、`migration-album.sql` 等），按需执行
- 存量数据迁移处理（标签系统：从 `posts.tags` 逗号分隔 → 标准关联表，用存储过程拆分）

### 5.8 定时任务

- `@EnableScheduling` + `@Scheduled`：文章定时发布（每 60 秒扫描 `scheduledAt` 到达的草稿）
- 未来扩展点：热点定时抓取、缓存预热、数据清理

### 5.9 容器化实践

- 多阶段构建：编译阶段用 `maven:3.9-eclipse-temurin-17`，运行阶段用 `eclipse-temurin:17-jre`（镜像更小）
- 环境分离：`application.yml` 默认 + `spring.config.activate.on-profile: prod` 生产覆盖
- Prod 环境下自动关闭 Swagger（`springdoc.api-docs.enabled: false`）

### 5.10 标签系统架构

- 标签独立表 `tag` + 多对多关联表 `post_tag`
- 标签 slug 唯一（小写 + 连字符），支持中文标签自动转换
- `tag.post_count` 冗余计数，避免频繁 COUNT 查询
- 兼容旧数据：初始化存储过程拆分 `posts.tags` 逗号分隔值为标准关联

---

## 六、编码风格约定

### 6.1 包命名

```
com.blog.aspect      — AOP 切面
com.blog.common      — 通用工具 / 基础类 / 枚举
com.blog.config      — Spring 配置类
com.blog.controller  — 控制器
com.blog.dto         — 数据传输对象（请求/响应）
com.blog.entity      — 数据库实体
com.blog.exception   — 异常类 + 全局处理器
com.blog.mapper      — MyBatis-Plus Mapper
com.blog.service     — 服务接口
com.blog.service.impl — 服务实现
```

### 6.2 命名规范

| 元素 | 规范 | 示例 |
|------|------|------|
| Controller 方法 | 名词性动词 | `list`, `detail`, `create`, `update`, `delete` |
| Service 方法 | 业务语义完整 | `getPublishedPosts`, `toggleLike`, `incrementViews` |
| DTO | 以 Request/Response 结尾 | `CreatePostRequest`, `PostResponse` |
| 实体 | 数据库表名映射 | `Post`, `User`, `Comment` |
| Mapper | 实体名 + Mapper | `PostMapper`, `UserMapper` |

### 6.3 常用注解组合

```java
// Controller
@RestController
@RequestMapping("/api/posts")
@Tag(name = "文章管理")

// Service
@Service
@Transactional(rollbackFor = Exception.class)

// Entity
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("posts")

// 分页查询
IPage<T> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper)
return Result.success(PageResult.of(page));
```

### 6.4 配置管理

```
# 环境变量/配置文件驱动，不硬编码
@Value("${upload.dir:./uploads}")     # 带默认值
@Value("${jwt.secret}")               # 必填
@Value("${jwt.expiration:86400000}")  # 数字带默认值

# Profile 分离
application.yml          # 公共配置
application-prod.yml     # 生产覆盖（Docker 启动时 --spring.profiles.active=prod）
```
