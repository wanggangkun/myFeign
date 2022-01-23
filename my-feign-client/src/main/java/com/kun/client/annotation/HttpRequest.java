package com.kun.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记可以通过http访问的服务
 *
 * @author kun
 * @data 2022/1/15 17:55
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequest {
    /**
     * http请求url
     *
     * @return  url
     */
    String value();
}
