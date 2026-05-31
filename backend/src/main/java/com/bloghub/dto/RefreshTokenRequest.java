package com.bloghub.dto;

import javax.validation.constraints.NotBlank;

/**
 * 刷新 Token 请求 DTO
 */
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
