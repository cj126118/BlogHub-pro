package com.bloghub.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bloghub.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类（含文章数）
     */
    @Select("SELECT c.*, (SELECT COUNT(*) FROM article a WHERE a.category_id = c.id AND a.deleted = 0 AND a.status = 'PUBLISHED') AS post_count FROM category c WHERE c.deleted = 0 ORDER BY c.sort_order ASC")
    List<Category> selectAllWithCount();
}
