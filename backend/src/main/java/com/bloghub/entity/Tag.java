package com.bloghub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bloghub.common.BaseEntity;

/**
 * 标签实体
 */
@TableName("tag")
public class Tag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private String slug;
    private Integer postCount;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
}
