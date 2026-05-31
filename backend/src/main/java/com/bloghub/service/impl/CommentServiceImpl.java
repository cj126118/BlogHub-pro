package com.bloghub.service.impl;

import com.bloghub.entity.Comment;
import com.bloghub.mapper.CommentMapper;
import com.bloghub.service.CommentService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> getArticleComments(Long articleId) {
        // 获取所有一级评论
        List<Comment> topComments = commentMapper.selectByArticleId(articleId);

        // 为每个一级评论获取子回复
        for (Comment comment : topComments) {
            List<Comment> replies = commentMapper.selectByParentId(comment.getId());
            comment.setChildren(replies);
        }

        return topComments;
    }

    @Override
    public Comment create(Comment comment) {
        // jsoup XSS 过滤
        String clean = Jsoup.clean(comment.getContent(), Safelist.none());
        comment.setContent(clean);

        comment.setStatus(1);
        comment.setDeleted(0);
        commentMapper.insert(comment);
        return comment;
    }
}
