package com.slfuture.pluto.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ResourceView {
    /**
     * 资源ID
     */
    int id() default 0;
    /**
     * 资源类
     */
    Class<?> clazz()  default Object.class;
    /**
     * 资源ID属性
     */
    String field() default "";
}
