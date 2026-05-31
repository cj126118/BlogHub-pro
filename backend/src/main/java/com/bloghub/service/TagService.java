package com.bloghub.service;

import com.bloghub.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    List<Tag> getAll();

    Tag getById(Long id);

    /**
     * 根据 slug 查询
     */
    Tag getBySlug(String slug);

    Tag create(Tag tag);

    Tag update(Tag tag);

    void delete(Long id);

    /**
     * 根据 ID 列表批量查询
     */
    List<Tag> getByIds(List<Long> ids);
}
