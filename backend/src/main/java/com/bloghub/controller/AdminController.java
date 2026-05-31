package com.bloghub.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bloghub.common.PageResult;
import com.bloghub.common.Result;
import com.bloghub.entity.Article;
import com.bloghub.entity.Comment;
import com.bloghub.entity.SysLog;
import com.bloghub.entity.User;
import com.bloghub.mapper.ArticleMapper;
import com.bloghub.mapper.CommentMapper;
import com.bloghub.mapper.LogMapper;
import com.bloghub.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private LogMapper logMapper;

    /**
     * 仪表盘统计数据
     */
    @GetMapping("/stats/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();

        long userCount = userMapper.selectCount(null);
        stats.put("userCount", userCount);

        long articleCount = articleMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Article>()
                        .eq(Article::getDeleted, 0));
        stats.put("articleCount", articleCount);

        long commentCount = commentMapper.selectCount(null);
        stats.put("commentCount", commentCount);

        // 最近 7 天注册用户数
        long newUsers = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .apply("created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)"));
        stats.put("newUsers", newUsers);

        return Result.success(stats);
    }

    /**
     * 用户列表（分页）
     */
    @GetMapping("/users")
    public Result<PageResult<User>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return Result.success(PageResult.of(userMapper.selectPage(pageParam, wrapper)));
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        Integer status = body.get("status") instanceof Number
                ? ((Number) body.get("status")).intValue() : 1;
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 评论列表（分页）
     */
    @GetMapping("/comments")
    public Result<PageResult<Comment>> comments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Comment> pageParam =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        return Result.success(PageResult.of(commentMapper.selectPageWithArticle(pageParam)));
    }

    /**
     * 隐藏/显示评论
     */
    @PutMapping("/comments/{id}/status")
    public Result<Void> toggleCommentStatus(@PathVariable Long id,
                                             @RequestBody Map<String, Object> body) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.notFound("评论不存在");
        }
        Integer status = body.get("status") instanceof Number
                ? ((Number) body.get("status")).intValue() : 1;
        comment.setStatus(status);
        commentMapper.updateById(comment);
        return Result.success();
    }

    /**
     * 操作日志列表（分页）
     */
    @GetMapping("/logs")
    public Result<PageResult<SysLog>> logs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SysLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysLog::getCreatedAt);
        return Result.success(PageResult.of(logMapper.selectPage(pageParam, wrapper)));
    }
}
