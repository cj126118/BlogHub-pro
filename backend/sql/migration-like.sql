-- BlogHub 点赞记录表
-- 用法: mysql -u root -p --default-character-set=utf8 bloghub < migration-like.sql

CREATE TABLE IF NOT EXISTS `like_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `article_id`    BIGINT       NOT NULL COMMENT '文章ID',
    `visitor_id`    VARCHAR(100) NOT NULL COMMENT '访客标识(user:{id} / visitor:{uuid})',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_visitor` (`article_id`, `visitor_id`),
    KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';
