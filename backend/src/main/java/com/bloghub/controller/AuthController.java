package com.bloghub.controller;

import com.bloghub.common.RateLimit;
import com.bloghub.common.Result;
import com.bloghub.config.JwtUtil;
import com.bloghub.dto.LoginRequest;
import com.bloghub.dto.LoginResponse;
import com.bloghub.dto.RefreshTokenRequest;
import com.bloghub.dto.RegisterRequest;
import com.bloghub.entity.User;
import com.bloghub.exception.BusinessException;
import com.bloghub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器 — 注册/登录/刷新 Token
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @RateLimit(key = "register", max = 3, period = 60)
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword());

        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        LoginResponse response = new LoginResponse(
                user.getId(), user.getUsername(), user.getNickname(),
                user.getAvatar(), user.getRole(), token, refreshToken
        );
        return Result.created(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @RateLimit(key = "login", max = 5, period = 60)
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());

        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        LoginResponse response = new LoginResponse(
                user.getId(), user.getUsername(), user.getNickname(),
                user.getAvatar(), user.getRole(), token, refreshToken
        );
        return Result.success(response);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new BusinessException("Refresh Token 无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());
        User user = userService.getById(userId);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String newToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        LoginResponse response = new LoginResponse(
                user.getId(), user.getUsername(), user.getNickname(),
                user.getAvatar(), user.getRole(), newToken, newRefreshToken
        );
        return Result.success(response);
    }

    /**
     * 获取当前用户信息（需登录）
     */
    @GetMapping("/me")
    public Result<LoginResponse> me(@RequestAttribute Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        LoginResponse response = new LoginResponse(
                user.getId(), user.getUsername(), user.getNickname(),
                user.getAvatar(), user.getRole(), null, null
        );
        return Result.success(response);
    }
}
