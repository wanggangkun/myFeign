package com.kun.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注方法参数
 *
 * @author kun
 * @data 2022/1/15 17:57
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiRequestBody {
    /**
     * 解析参数时用到JSON中的key
     *
     * @return  JSON格式参数
     */
    String value();
}
