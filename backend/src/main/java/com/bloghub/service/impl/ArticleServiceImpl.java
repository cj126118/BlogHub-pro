package com.bloghub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bloghub.entity.Article;
import com.bloghub.exception.BusinessException;
import com.bloghub.exception.ResourceNotFoundException;
import com.bloghub.mapper.ArticleMapper;
import com.bloghub.mapper.TagMapper;
import java.time.LocalDateTime;
import com.bloghub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 文章服务实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public IPage<Article> getPublishedPage(int page, int size, Long categoryId, Long tagId, String keyword, String sort) {
        Page<Article> pageParam = new Page<>(page, size);
        return articleMapper.selectPublishedPage(pageParam, categoryId, tagId, keyword, sort);
    }

    @Override
    public Article getDetail(Long id) {
        Article article = articleMapper.selectDetailById(id);
        if (article == null) {
            throw new ResourceNotFoundException("文章", id);
        }
        return article;
    }

    @Override
    public Article getDetailBySlug(String slug) {
        Article article = articleMapper.selectDetailBySlug(slug);
        if (article == null) {
            throw new ResourceNotFoundException("文章不存在: " + slug);
        }
        return article;
    }

    @Override
    public Article create(Article article, List<Long> tagIds, Long authorId) {
        // 生成 slug
        if (article.getSlug() == null || article.getSlug().isEmpty()) {
            article.setSlug(generateSlug(article.getTitle()));
        }

        article.setAuthorId(authorId);
        article.setStatus(article.getStatus() != null ? article.getStatus() : "DRAFT");
        article.setViews(0);
        article.setLikes(0);
        article.setIsPinned(0);
        article.setDeleted(0);

        articleMapper.insert(article);

        // 处理标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                articleMapper.insertArticleTag(article.getId(), tagId);
                tagMapper.incrementPostCount(tagId);
            }
        }

        return articleMapper.selectDetailById(article.getId());
    }

    @Override
    public Article update(Article article, List<Long> tagIds) {
        Article existing = articleMapper.selectById(article.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("文章", article.getId());
        }

        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);

        // 重新处理标签
        if (tagIds != null) {
            // 获取旧标签，减少计数
            List<Long> oldTagIds = articleMapper.selectTagIdsByArticleId(article.getId());
            for (Long oldTagId : oldTagIds) {
                tagMapper.decrementPostCount(oldTagId);
            }

            // 删除旧关联
            articleMapper.deleteArticleTags(article.getId());

            // 插入新关联
            for (Long tagId : tagIds) {
                articleMapper.insertArticleTag(article.getId(), tagId);
                tagMapper.incrementPostCount(tagId);
            }
        }

        return articleMapper.selectDetailById(article.getId());
    }

    @Override
    public void delete(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new ResourceNotFoundException("文章", id);
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException("只能删除自己的文章");
        }

        // 逻辑删除
        articleMapper.deleteById(id);

        // 减少标签计数
        List<Long> tagIds = articleMapper.selectTagIdsByArticleId(id);
        for (Long tagId : tagIds) {
            tagMapper.decrementPostCount(tagId);
        }
        articleMapper.deleteArticleTags(id);
    }

    @Override
    public IPage<Article> getUserPosts(Long authorId, int page, int size) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getAuthorId, authorId)
               .eq(Article::getDeleted, 0)
               .orderByDesc(Article::getCreatedAt);
        return articleMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void incrementViews(Long id) {
        articleMapper.incrementViews(id);
    }

    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        if (base.length() > 80) {
            base = base.substring(0, 80);
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
