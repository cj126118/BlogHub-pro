package com.bloghub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bloghub.entity.Category;
import com.bloghub.exception.BusinessException;
import com.bloghub.exception.ResourceNotFoundException;
import com.bloghub.mapper.CategoryMapper;
import com.bloghub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAll() {
        return categoryMapper.selectAllWithCount();
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            throw new ResourceNotFoundException("分类", id);
        }
        return category;
    }

    @Override
    public Category create(Category category) {
        // 检查 slug 唯一性
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getSlug, category.getSlug());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("分类别名已存在");
        }
        category.setDeleted(0);
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(Category category) {
        Category existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("分类", category.getId());
        }
        categoryMapper.updateById(category);
        return categoryMapper.selectById(category.getId());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new ResourceNotFoundException("分类", id);
        }
        categoryMapper.deleteById(id);
    }
}
