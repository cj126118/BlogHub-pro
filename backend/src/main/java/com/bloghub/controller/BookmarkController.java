package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.entity.Article;
import com.bloghub.entity.Bookmark;
import com.bloghub.mapper.BookmarkMapper;
import com.bloghub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Autowired
    private BookmarkMapper bookmarkMapper;

    @Autowired
    private ArticleService articleService;

    /**
     * 获取当前用户的收藏列表
     */
    @GetMapping
    public Result<List<Bookmark>> list(@RequestAttribute Long userId) {
        return Result.success(bookmarkMapper.selectByUserIdWithArticle(userId));
    }

    /**
     * 切换收藏
     */
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Object> body,
                                               @RequestAttribute Long userId) {
        Object slugObj = body.get("slug");
        if (slugObj == null) {
            return Result.badRequest("缺少 slug 参数");
        }
        Article article = articleService.getDetailBySlug(slugObj.toString());

        boolean exists = bookmarkMapper.exists(userId, article.getId());
        if (exists) {
            // 取消收藏
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Bookmark> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(Bookmark::getUserId, userId)
                   .eq(Bookmark::getArticleId, article.getId());
            bookmarkMapper.delete(wrapper);
        } else {
            // 添加收藏
            Bookmark bookmark = new Bookmark();
            bookmark.setUserId(userId);
            bookmark.setArticleId(article.getId());
            bookmarkMapper.insert(bookmark);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("bookmarked", !exists);
        return Result.success(data);
    }
}
