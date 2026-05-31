package com.bloghub.service;

import com.bloghub.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    List<Category> getAll();

    Category getById(Long id);

    Category create(Category category);

    Category update(Category category);

    void delete(Long id);
}
