package com.bloghub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bloghub.common.BaseEntity;

/**
 * 分类实体
 */
@TableName("category")
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private String slug;
    private String description;
    private Integer sortOrder;
    private Integer deleted;

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer postCount;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
}
