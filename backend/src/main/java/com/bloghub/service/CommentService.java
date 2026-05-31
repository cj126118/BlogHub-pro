package com.bloghub.service;

import com.bloghub.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 获取文章的评论列表（含嵌套回复）
     */
    List<Comment> getArticleComments(Long articleId);

    /**
     * 发表评论
     */
    Comment create(Comment comment);
}
