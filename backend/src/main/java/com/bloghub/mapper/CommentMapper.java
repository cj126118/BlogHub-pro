package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询文章的一级评论（按时间倒序）
     */
    List<Comment> selectByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询某评论的子回复
     */
    List<Comment> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 分页查询评论（关联文章标题/slug 和作者名）
     */
    com.baomidou.mybatisplus.core.metadata.IPage<Comment> selectPageWithArticle(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Comment> page);
}
