package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.config.JwtUtil;
import com.bloghub.entity.Article;
import com.bloghub.entity.Comment;
import com.bloghub.service.ArticleService;
import com.bloghub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取文章的评论列表
     */
    @GetMapping("/posts/{slug}/comments")
    public Result<List<Comment>> list(@PathVariable String slug) {
        Article article = articleService.getDetailBySlug(slug);
        return Result.success(commentService.getArticleComments(article.getId()));
    }

    /**
     * 发表评论
     */
    @PostMapping("/posts/{slug}/comments")
    public Result<Comment> create(@PathVariable String slug,
                                   @RequestBody Map<String, Object> body,
                                   HttpServletRequest request) {
        Article article = articleService.getDetailBySlug(slug);

        Comment comment = new Comment();
        comment.setArticleId(article.getId());

        // 支持嵌套回复
        Object parentId = body.get("parentId");
        if (parentId != null) {
            comment.setParentId(Long.valueOf(parentId.toString()));
        }
        comment.setReplyToName((String) body.get("replyToName"));

        // 优先取登录用户信息（手动解析 Token，因为评论接口跳过了拦截器）
        Long userId = parseUserIdFromToken(request);
        if (userId != null) {
            comment.setUserId(userId);
        }

        comment.setNickname((String) body.get("nickname"));
        comment.setEmail((String) body.get("email"));
        comment.setContent((String) body.get("content"));

        Comment created = commentService.create(comment);
        return Result.created(created);
    }

    /**
     * 删除评论（管理员或本人）
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestAttribute Long userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.notFound("评论不存在");
        }
        if (comment.getUserId() != null && !comment.getUserId().equals(userId)) {
            return Result.fail(403, "无权删除此评论");
        }
        commentMapper.deleteById(id);
        return Result.noContent();
    }

    @Autowired
    private com.bloghub.mapper.CommentMapper commentMapper;

    /**
     * 从请求中手动解析 JWT Token 获取 userId
     */
    private Long parseUserIdFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        try {
            String token = auth.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
