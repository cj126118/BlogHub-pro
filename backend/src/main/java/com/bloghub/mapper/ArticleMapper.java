package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bloghub.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章 Mapper
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询已发布的文章（关联作者名+分类名）
     */
    IPage<Article> selectPublishedPage(Page<Article> page, @Param("categoryId") Long categoryId,
                                       @Param("tagId") Long tagId, @Param("keyword") String keyword,
                                       @Param("sort") String sort);

    /**
     * 查询文章详情（关联作者名+分类名）
     */
    Article selectDetailById(@Param("id") Long id);

    /**
     * 按 slug 查询详情
     */
    Article selectDetailBySlug(@Param("slug") String slug);

    /**
     * 获取文章的标签 ID 列表
     */
    @Select("SELECT tag_id FROM article_tag WHERE article_id = #{articleId}")
    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);

    /**
     * 插入文章-标签关联
     */
    void insertArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    /**
     * 删除文章-标签关联
     */
    void deleteArticleTags(@Param("articleId") Long articleId);

    /**
     * 增加浏览量
     */
    void incrementViews(@Param("id") Long id);

    /**
     * 更新点赞数
     */
    void updateLikes(@Param("id") Long id, @Param("likes") Integer likes);
}
