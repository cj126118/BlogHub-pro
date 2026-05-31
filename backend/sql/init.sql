-- ===========================================================
-- BlogHub 数据库初始化
-- 用法: mysql -u root -p --default-character-set=utf8 < init.sql
-- ⚠️ Windows 下必须加 --default-character-set=utf8，否则中文乱码
-- ===========================================================

CREATE DATABASE IF NOT EXISTS bloghub
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE bloghub;

-- -----------------------------------------------------------
-- 用户表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`      VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`      VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `email`         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar`        VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    `role`          VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色: user/admin',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    `description`   VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删 1-已删',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 文章表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `article` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `slug`          VARCHAR(200) NOT NULL COMMENT 'URL 别名',
    `title`         VARCHAR(255) NOT NULL COMMENT '标题',
    `content`       LONGTEXT     DEFAULT NULL COMMENT 'Markdown 内容',
    `description`   VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    `cover_image`   VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `author_id`     BIGINT       NOT NULL COMMENT '作者ID',
    `category_id`   BIGINT       DEFAULT NULL COMMENT '分类ID',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/SCHEDULED',
    `is_pinned`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶: 0-否 1-是',
    `views`         INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    `likes`         INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    `scheduled_at`  DATETIME     DEFAULT NULL COMMENT '定时发布时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删 1-已删',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_views` (`views`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- -----------------------------------------------------------
-- 分类表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `category` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`          VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `slug`          VARCHAR(100) NOT NULL COMMENT 'URL 别名',
    `description`   VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `sort_order`    INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- -----------------------------------------------------------
-- 标签表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tag` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`          VARCHAR(50)  NOT NULL COMMENT '标签名称',
    `slug`          VARCHAR(100) NOT NULL COMMENT 'URL 别名',
    `post_count`    INT          NOT NULL DEFAULT 0 COMMENT '关联文章数',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_post_count` (`post_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- -----------------------------------------------------------
-- 文章-标签 关联表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `article_tag` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `article_id`    BIGINT       NOT NULL COMMENT '文章ID',
    `tag_id`        BIGINT       NOT NULL COMMENT '标签ID',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- -----------------------------------------------------------
-- 评论表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comment` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id`    BIGINT       NOT NULL COMMENT '文章ID',
    `parent_id`     BIGINT       DEFAULT NULL COMMENT '父评论ID(支持嵌套回复)',
    `user_id`       BIGINT       DEFAULT NULL COMMENT '用户ID(可空,访客用昵称+邮箱)',
    `nickname`      VARCHAR(50)  DEFAULT NULL COMMENT '访客昵称',
    `email`         VARCHAR(100) DEFAULT NULL COMMENT '访客邮箱',
    `content`       TEXT         NOT NULL COMMENT '评论内容(HTML净化后)',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-隐藏',
    `deleted`       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- -----------------------------------------------------------
-- 系统日志表（审计用）
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id`       BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    `action`        VARCHAR(100) NOT NULL COMMENT '操作描述',
    `resource`      VARCHAR(100) DEFAULT NULL COMMENT '操作资源',
    `resource_id`   BIGINT       DEFAULT NULL COMMENT '资源ID',
    `ip`            VARCHAR(50)  DEFAULT NULL COMMENT '请求IP',
    `detail`        TEXT         DEFAULT NULL COMMENT '操作详情',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_action` (`action`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- -----------------------------------------------------------
-- 默认管理员 (密码: admin123)
-- -----------------------------------------------------------
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `status`)
VALUES (1, 'admin',
        '$2a$10$stFBK2WjSasg1dpIWksE7.SgKoAy7kwMosN2Ms9umlqC6nCmAcXL6',
        '管理员', 'admin', 1);

-- -----------------------------------------------------------
-- 默认分类
-- -----------------------------------------------------------
INSERT IGNORE INTO `category` (`id`, `name`, `slug`, `sort_order`) VALUES
    (1, '技术', 'tech', 1),
    (2, '生活', 'life', 2),
    (3, '随笔', 'essay', 3),
    (4, '教程', 'tutorial', 4);
