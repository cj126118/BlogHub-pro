package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 点赞记录 Mapper
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    /**
     * 查询某文章的点赞数
     */
    @Select("SELECT COUNT(*) FROM like_record WHERE article_id = #{articleId}")
    int countByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询某访客是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM like_record WHERE article_id = #{articleId} AND visitor_id = #{visitorId}")
    boolean exists(@Param("articleId") Long articleId, @Param("visitorId") String visitorId);
}
