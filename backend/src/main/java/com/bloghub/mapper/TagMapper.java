package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 标签 Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 递增标签文章数
     */
    @Update("UPDATE tag SET post_count = post_count + 1 WHERE id = #{tagId}")
    void incrementPostCount(Long tagId);

    /**
     * 递减标签文章数
     */
    @Update("UPDATE tag SET post_count = post_count - 1 WHERE id = #{tagId} AND post_count > 0")
    void decrementPostCount(Long tagId);
}
