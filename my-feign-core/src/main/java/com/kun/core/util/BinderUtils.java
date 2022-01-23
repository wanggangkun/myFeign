package com.kun.core.util;

import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author kun
 * @data 2022/1/16 14:10
 */
public class BinderUtils {
    private static boolean exitBinder = false;
    private static boolean exitRelaxedDataBinder = false;
    private static Method binderGetMethod;
    private static Method binderBindMethod;
    private static Method bindableOfMethod;
    private static Method bindResultOrElseMethod;

    static {
        try {
            Class.forName("org.springframework.boot.context.properties.bind.Binder");
            exitBinder = true;
        } catch (ClassNotFoundException ignore){

        }
        try {
            Class.forName("org.springframework.boot.bind.RelaxedDataBinder");
            exitRelaxedDataBinder = true;
        } catch (ClassNotFoundException ignore){

        }
    }

    public static <T> T bind(ConfigurableEnvironment environment, String prefix, Class<T> type) {
        if (exitRelaxedDataBinder) {
            return relaxedDataBinderBind(environment, prefix, type);
        } else if (exitBinder) {
            return binderBind(environment, prefix, type);
        } else {
            throw new IllegalStateException(
                    "Can not find class org.springframework.boot.context.properties.bind.Binder or org.springframework.boot.bind.RelaxedDataBinder"
            );
        }
    }

    private static <T> T relaxedDataBinderBind(ConfigurableEnvironment environment, String prefix, Class<T> type) {
        T instant;
        try {
            instant = type.newInstance();
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        new RelaxedDataBinder(instant, prefix).bind(new PropertySourcesPropertyValues(environment.getPropertySources()));
        return instant;
    }

    private static <T> T binderBind(ConfigurableEnvironment environment, String prefix, Class<T> type) {
        try {
            if (Objects.isNull(binderGetMethod)) {
                Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
                Class<?> bindableClass = Class.forName("org.springframework.boot.context.properties.bind.Bindable");
                binderGetMethod = binderClass.getMethod("get", Environment.class);
                binderBindMethod = binderClass.getMethod("bind", String.class, bindableClass);
            }
            if (Objects.isNull(bindableOfMethod)) {
                bindableOfMethod = Class.forName("org.springframework.boot.context.properties.bind.Bindable")
                        .getMethod("of", Class.class);
            }
            if (Objects.isNull(bindResultOrElseMethod)) {
                bindResultOrElseMethod = Class.forName("org.springframework.boot.context.properties.bind.BindResult")
                        .getMethod("orElse", Object.class);
            }
            Object binder = binderGetMethod.invoke(null, environment);
            Object bindable = bindableOfMethod.invoke(null, type);
            Object bindResult = binderBindMethod.invoke(binder, prefix, bindable);
            return (T) bindResultOrElseMethod.invoke(bindResult, type.newInstance());
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }
}
