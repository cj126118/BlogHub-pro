package com.bloghub.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.bloghub.common.BaseEntity;

import java.util.List;

/**
 * 评论实体
 */
@TableName("comment")
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long articleId;
    private Long parentId;
    private String replyToName;
    private Long userId;
    private String nickname;
    private String email;
    private String content;
    private Integer status;
    private Integer deleted;

    /** 子评论列表（非数据库字段） */
    @TableField(exist = false)
    private List<Comment> children;

    /** 作者名（非数据库字段） */
    @TableField(exist = false)
    private String authorName;

    /** 文章标题（非数据库字段） */
    @TableField(exist = false)
    private String articleTitle;

    /** 文章 slug（非数据库字段） */
    @TableField(exist = false)
    private String articleSlug;

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getReplyToName() { return replyToName; }
    public void setReplyToName(String replyToName) { this.replyToName = replyToName; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public List<Comment> getChildren() { return children; }
    public void setChildren(List<Comment> children) { this.children = children; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    public String getArticleSlug() { return articleSlug; }
    public void setArticleSlug(String articleSlug) { this.articleSlug = articleSlug; }
}
