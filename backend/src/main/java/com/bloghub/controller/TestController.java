package com.bloghub.controller;

import com.bloghub.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 — 验证 JWT 拦截器是否生效
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 公开接口 — 无需登录
     */
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("这是一个公开接口");
    }

    /**
     * 受保护接口 — 需要登录
     */
    @GetMapping("/protected")
    public Result<Map<String, Object>> protectedEndpoint(
            @RequestAttribute Long userId,
            @RequestAttribute String username,
            @RequestAttribute String role) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("role", role);
        data.put("message", "认证通过！这是受保护接口");
        return Result.success(data);
    }
}
