package com.bloghub.controller;

import com.bloghub.common.PageResult;
import com.bloghub.common.Result;
import com.bloghub.entity.Article;
import com.bloghub.entity.Tag;
import com.bloghub.service.ArticleService;
import com.bloghub.service.TagService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/posts")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TagService tagService;

    /**
     * 分页查询已发布文章（公开）
     */
    @GetMapping
    public Result<PageResult<Article>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort) {
        return Result.success(PageResult.of(
                articleService.getPublishedPage(page, size, categoryId, tagId, keyword, sort)));
    }

    /**
     * 文章归档（按时间倒序）
     */
    @GetMapping("/archive")
    public Result<List<Article>> archive() {
        Page<Article> page = new Page<>(1, 100);
        IPage<Article> result = articleService.getPublishedPage(1, 100, null, null, null, "date");
        return Result.success(result.getRecords());
    }

    @GetMapping("/ranking/likes")
    public Result<PageResult<Article>> ranking() {
        return Result.success(PageResult.of(
                articleService.getPublishedPage(1, 10, null, null, null, "likes")));
    }

    /**
     * 查询文章详情（按 slug）
     */
    @GetMapping("/{slug}")
    public Result<Article> detail(@PathVariable String slug) {
        Article article = articleService.getDetailBySlug(slug);
        articleService.incrementViews(article.getId());
        return Result.success(article);
    }

    /**
     * 创建文章（需登录）
     */
    @PostMapping
    public Result<Article> create(@RequestBody Map<String, Object> body,
                                   @RequestAttribute Long userId) {
        Article article = new Article();
        article.setTitle((String) body.get("title"));
        article.setSlug((String) body.get("slug"));
        article.setContent((String) body.get("content"));
        article.setDescription((String) body.get("description"));
        article.setCoverImage((String) body.get("coverImage"));
        article.setCategoryId(body.get("categoryId") != null
                ? Long.valueOf(body.get("categoryId").toString()) : null);
        article.setStatus((String) body.get("status"));

        List<Long> tagIds = resolveTagIds(body.get("tagIds"));
        Article created = articleService.create(article, tagIds, userId);
        return Result.created(created);
    }

    /**
     * 更新文章（需登录 + 本人）
     */
    @PutMapping("/{slug}")
    public Result<Article> update(@PathVariable String slug, @RequestBody Map<String, Object> body,
                                   @RequestAttribute Long userId) {
        Article existing = articleService.getDetailBySlug(slug);
        Article article = new Article();
        article.setId(existing.getId());
        article.setTitle((String) body.get("title"));
        article.setSlug((String) body.get("slug"));
        article.setContent((String) body.get("content"));
        article.setDescription((String) body.get("description"));
        article.setCoverImage((String) body.get("coverImage"));
        article.setCategoryId(body.get("categoryId") != null
                ? Long.valueOf(body.get("categoryId").toString()) : null);
        article.setStatus((String) body.get("status"));

        List<Long> tagIds = resolveTagIds(body.get("tagIds"));
        return Result.success(articleService.update(article, tagIds));
    }

    /**
     * 删除文章（需登录 + 本人）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestAttribute Long userId) {
        articleService.delete(id, userId);
        return Result.noContent();
    }

    /**
     * 获取当前用户的文章列表
     */
    @GetMapping("/my")
    public Result<PageResult<Article>> myPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestAttribute Long userId) {
        return Result.success(PageResult.of(
                articleService.getUserPosts(userId, page, size)));
    }

    /**
     * 将前端传的 tagIds（可能是数字ID或标签名字符串）统一转为 Long ID 列表
     */
    private List<Long> resolveTagIds(Object rawTagIds) {
        List<Long> result = new java.util.ArrayList<>();
        if (!(rawTagIds instanceof List)) return result;

        for (Object o : (List<?>) rawTagIds) {
            if (o instanceof Number) {
                // 数字 → 直接作为 tag ID
                result.add(((Number) o).longValue());
            } else if (o instanceof String) {
                String name = ((String) o).trim();
                if (name.isEmpty()) continue;
                // 字符串 → 按标签名查找或创建
                String slug = name.toLowerCase().replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "-")
                        .replaceAll("-+", "-").replaceAll("^-|-$", "");
                Tag tag = tagService.getBySlug(slug);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(name);
                    tag.setSlug(slug);
                    tag = tagService.create(tag);
                }
                result.add(tag.getId());
            }
        }
        return result;
    }
}
