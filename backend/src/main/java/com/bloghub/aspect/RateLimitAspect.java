package com.bloghub.aspect;

import com.bloghub.common.RateLimit;
import com.bloghub.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流切面 — 基于 IP + key 的本地计数器限流
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitAspect.class);

    /** 限流计数器存储：key_IP → 当前计数 */
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    /** 时间窗口起始时间：key_IP → 窗口开始时间戳 */
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 获取客户端 IP
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        String ip = getClientIp(request);

        String key = rateLimit.key() + "_" + ip;
        int max = rateLimit.max();
        long period = rateLimit.period() * 1000L; // 转毫秒
        long now = System.currentTimeMillis();

        // 检查是否需要重置窗口
        Long startTime = windowStart.get(key);
        if (startTime == null || (now - startTime) > period) {
            // 新窗口
            counters.put(key, new AtomicInteger(1));
            windowStart.put(key, now);
        } else {
            int count = counters.get(key).incrementAndGet();
            if (count > max) {
                log.warn("限流触发: key={}, ip={}, count={}", rateLimit.key(), ip, count);
                throw new BusinessException(429, "请求过于频繁，请稍后再试");
            }
        }

        return joinPoint.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
