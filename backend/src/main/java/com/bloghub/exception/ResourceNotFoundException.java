package com.bloghub.exception;

import com.bloghub.common.enums.ErrorCode;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String resource, Object id) {
        super(ErrorCode.NOT_FOUND, resource + " 不存在: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND.getCode(), message);
    }
}
