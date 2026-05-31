package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.entity.Article;
import com.bloghub.entity.LikeRecord;
import com.bloghub.mapper.LikeRecordMapper;
import com.bloghub.mapper.ArticleMapper;
import com.bloghub.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api/posts")
public class LikeController {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleService articleService;

    /**
     * 获取点赞状态和数量
     */
    @GetMapping("/{slug}/like")
    public Result<Map<String, Object>> status(@PathVariable String slug,
                                               HttpServletRequest request) {
        Article article = articleService.getDetailBySlug(slug);
        String visitorId = getVisitorId(request);

        boolean liked = likeRecordMapper.exists(article.getId(), visitorId);
        int count = likeRecordMapper.countByArticleId(article.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("count", count);
        return Result.success(data);
    }

    /**
     * 切换点赞
     */
    @PostMapping("/{slug}/like")
    public Result<Map<String, Object>> toggle(@PathVariable String slug,
                                               HttpServletRequest request) {
        Article article = articleService.getDetailBySlug(slug);
        String visitorId = getVisitorId(request);

        boolean exists = likeRecordMapper.exists(article.getId(), visitorId);
        if (exists) {
            // 取消点赞
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LikeRecord> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(LikeRecord::getArticleId, article.getId())
                   .eq(LikeRecord::getVisitorId, visitorId);
            likeRecordMapper.delete(wrapper);
        } else {
            // 点赞
            LikeRecord record = new LikeRecord();
            record.setArticleId(article.getId());
            record.setVisitorId(visitorId);
            likeRecordMapper.insert(record);
        }

        int count = likeRecordMapper.countByArticleId(article.getId());
        // 更新文章点赞数字段
        articleMapper.updateLikes(article.getId(), count);

        Map<String, Object> data = new HashMap<>();
        data.put("liked", !exists);
        data.put("count", count);
        return Result.success(data);
    }

    /**
     * 从请求中获取访客标识
     */
    private String getVisitorId(HttpServletRequest request) {
        String visitorId = request.getHeader("X-Visitor-Id");
        if (visitorId == null || visitorId.isEmpty()) {
            visitorId = "visitor_" + request.getRemoteAddr();
        }
        return visitorId;
    }
}
