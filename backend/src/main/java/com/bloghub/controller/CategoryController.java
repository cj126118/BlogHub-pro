package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.entity.Category;
import com.bloghub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.getAll());
    }

    /**
     * 获取单个分类
     */
    @GetMapping("/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    /**
     * 创建分类（需登录）
     */
    @PostMapping
    public Result<Category> create(@Valid @RequestBody Category category) {
        return Result.created(categoryService.create(category));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @Valid @RequestBody Category category) {
        category.setId(id);
        return Result.success(categoryService.update(category));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.noContent();
    }
}
