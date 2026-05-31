package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏 Mapper
 */
@Mapper
public interface BookmarkMapper extends BaseMapper<Bookmark> {

    /**
     * 查询用户的收藏列表（含文章信息）
     */
    @Select("SELECT b.*, a.title, a.slug FROM bookmark b LEFT JOIN article a ON b.article_id = a.id WHERE b.user_id = #{userId} ORDER BY b.created_at DESC")
    List<Bookmark> selectByUserIdWithArticle(@Param("userId") Long userId);

    /**
     * 查询用户是否已收藏某文章
     */
    @Select("SELECT COUNT(*) > 0 FROM bookmark WHERE user_id = #{userId} AND article_id = #{articleId}")
    boolean exists(@Param("userId") Long userId, @Param("articleId") Long articleId);
}
