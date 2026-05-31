package com.bloghub.exception;

import com.bloghub.common.Result;
import com.bloghub.common.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验失败 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.fail(ErrorCode.VALID_ERROR.getCode(), message);
    }

    /**
     * 缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParamException(MissingServletRequestParameterException e) {
        return Result.badRequest("缺少参数: " + e.getParameterName());
    }

    /**
     * 请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        return Result.badRequest("请求体格式错误");
    }

    /**
     * 兜底异常 — 打印完整请求信息帮助排查
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        String qs = request.getQueryString();
        String params = (qs != null) ? "?" + qs : "";
        log.error("未捕获异常: {} {}{} | 来源IP={} | 异常类型={} | 消息={}",
                request.getMethod(), request.getRequestURI(), params,
                request.getRemoteAddr(),
                e.getClass().getSimpleName(), e.getMessage());
        if (log.isDebugEnabled()) {
            // DEBUG 级别打印完整堆栈 + Header 信息
            log.debug("完整请求头:", (Object) java.util.Collections.list(request.getHeaderNames())
                    .stream().collect(java.util.stream.Collectors.toMap(
                            h -> h, h -> request.getHeader(h))));
            log.debug("完整异常堆栈:", e);
        }
        return Result.error("服务器内部错误");
    }
}
