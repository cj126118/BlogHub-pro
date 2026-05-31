package com.bloghub.aspect;

import com.bloghub.common.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 日志切面 — 记录 Service 层方法执行耗时 + 入参
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.bloghub.service..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            log.warn("[{}] {}.{} 异常: {}", className, className, methodName, e.getMessage());
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed > 200) {
                log.warn("[慢查询] {}.{} 耗时: {}ms", className, methodName, elapsed);
            } else {
                log.debug("[{}] {}.{} 耗时: {}ms", className, className, methodName, elapsed);
            }
        }
    }
}
