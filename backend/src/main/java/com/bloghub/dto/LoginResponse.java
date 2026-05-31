package com.bloghub.dto;

/**
 * 登录响应 DTO
 */
public class LoginResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
    private String token;
    private String refreshToken;

    public LoginResponse() {}

    public LoginResponse(Long userId, String username, String nickname, String avatar,
                         String role, String token, String refreshToken) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
