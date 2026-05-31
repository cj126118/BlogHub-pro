package com.bloghub.controller;

import com.bloghub.common.Result;
import com.bloghub.entity.User;
import com.bloghub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器 — 个人信息
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> profile(@RequestAttribute Long userId) {
        return Result.success(userService.getById(userId));
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestAttribute Long userId,
                                       @RequestBody Map<String, Object> body) {
        User user = userService.getById(userId);
        if (body.containsKey("nickname")) {
            user.setNickname((String) body.get("nickname"));
        }
        if (body.containsKey("email")) {
            user.setEmail((String) body.get("email"));
        }
        if (body.containsKey("description")) {
            user.setDescription((String) body.get("description"));
        }
        if (body.containsKey("avatar")) {
            user.setAvatar((String) body.get("avatar"));
        }
        userService.update(user);
        return Result.success(userService.getById(userId));
    }

    /**
     * 获取用户公开信息
     */
    @GetMapping("/{id}")
    public Result<User> publicInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        // 只返回公开信息
        User safe = new User();
        safe.setId(user.getId());
        safe.setUsername(user.getUsername());
        safe.setNickname(user.getNickname());
        safe.setAvatar(user.getAvatar());
        safe.setDescription(user.getDescription());
        return Result.success(safe);
    }
}
