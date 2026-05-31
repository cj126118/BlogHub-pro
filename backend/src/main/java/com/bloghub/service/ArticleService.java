package com.bloghub.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bloghub.entity.Article;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 分页查询已发布文章
     */
    IPage<Article> getPublishedPage(int page, int size, Long categoryId, Long tagId, String keyword, String sort);

    /**
     * 查询文章详情
     */
    Article getDetail(Long id);

    /**
     * 按 slug 查询详情
     */
    Article getDetailBySlug(String slug);

    /**
     * 创建文章（含标签处理）
     */
    Article create(Article article, List<Long> tagIds, Long authorId);

    /**
     * 更新文章（含标签处理）
     */
    Article update(Article article, List<Long> tagIds);

    /**
     * 删除文章（逻辑删除）
     */
    void delete(Long id, Long userId);

    /**
     * 获取作者的文章列表
     */
    IPage<Article> getUserPosts(Long authorId, int page, int size);

    /**
     * 增加浏览量
     */
    void incrementViews(Long id);
}
