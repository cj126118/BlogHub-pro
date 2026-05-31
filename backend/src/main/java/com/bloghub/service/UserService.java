package com.bloghub.service;

import com.bloghub.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(String username, String password);

    /**
     * 用户登录（返回 User 对象，含完整信息）
     */
    User login(String username, String password);

    /**
     * 根据用户名查询
     */
    User getByUsername(String username);

    /**
     * 根据 ID 查询
     */
    User getById(Long id);

    /**
     * 更新用户信息
     */
    void update(User user);
}
