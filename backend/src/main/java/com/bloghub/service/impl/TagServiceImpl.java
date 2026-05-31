package com.bloghub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bloghub.entity.Tag;
import com.bloghub.exception.BusinessException;
import com.bloghub.exception.ResourceNotFoundException;
import com.bloghub.mapper.TagMapper;
import com.bloghub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getAll() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getPostCount);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public Tag getBySlug(String slug) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Tag> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(Tag::getSlug, slug);
        return tagMapper.selectOne(wrapper);
    }

    @Override
    public Tag getById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new ResourceNotFoundException("标签", id);
        }
        return tag;
    }

    @Override
    public Tag create(Tag tag) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getSlug, tag.getSlug());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("标签别名已存在");
        }
        tag.setPostCount(0);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public Tag update(Tag tag) {
        Tag existing = tagMapper.selectById(tag.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("标签", tag.getId());
        }
        tagMapper.updateById(tag);
        return tagMapper.selectById(tag.getId());
    }

    @Override
    public void delete(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new ResourceNotFoundException("标签", id);
        }
        tagMapper.deleteById(id);
    }

    @Override
    public List<Tag> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return tagMapper.selectBatchIds(ids);
    }
}
