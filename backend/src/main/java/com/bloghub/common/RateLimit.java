package com.bloghub.common;

import java.lang.annotation.*;

/**
 * 限流注解 — 标注在需要限流的接口方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 限流 key（区分不同接口） */
    String key() default "";

    /** 时间窗口内最大请求数 */
    int max() default 5;

    /** 时间窗口（秒） */
    int period() default 60;
}
