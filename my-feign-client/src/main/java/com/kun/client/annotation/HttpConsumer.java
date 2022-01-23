package com.kun.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要代理为http请求发送
 *
 * @author kun
 * @data 2022/1/15 17:50
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpConsumer {
    /**
     * 域名
     *
     * @return  域名
     */
    String domain();

    /**
     * 端口，默认80
     *
     * @return  端口号
     */
    String port() default "80";
}
