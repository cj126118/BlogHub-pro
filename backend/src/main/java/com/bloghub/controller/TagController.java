package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.entity.Tag;
import com.bloghub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.getAll());
    }

    /**
     * 获取单个标签
     */
    @GetMapping("/{id}")
    public Result<Tag> detail(@PathVariable Long id) {
        return Result.success(tagService.getById(id));
    }

    /**
     * 创建标签（需登录）
     */
    @PostMapping
    public Result<Tag> create(@Valid @RequestBody Tag tag) {
        return Result.created(tagService.create(tag));
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
    public Result<Tag> update(@PathVariable Long id, @Valid @RequestBody Tag tag) {
        tag.setId(id);
        return Result.success(tagService.update(tag));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.noContent();
    }
}
