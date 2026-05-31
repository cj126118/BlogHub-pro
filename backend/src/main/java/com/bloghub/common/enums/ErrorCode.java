package com.bloghub.common.enums;

/**
 * 统一响应码枚举
 */
public enum ErrorCode {

    SUCCESS(200, "success"),
    CREATED(201, "created"),
    NO_CONTENT(204, "no content"),
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found"),
    VALID_ERROR(422, "validation error"),
    RATE_LIMIT(429, "too many requests"),
    INTERNAL_ERROR(500, "internal server error"),
    SERVICE_UNAVAILABLE(503, "service unavailable");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
