package com.bloghub.common;

import java.lang.annotation.*;

/**
 * 日志注解 — 用于需要记录操作日志的接口方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 操作描述 */
    String value() default "";

    /** 资源类型 */
    String resource() default "";
}
