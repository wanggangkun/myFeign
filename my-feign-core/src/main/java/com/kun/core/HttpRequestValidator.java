package com.kun.core;

import com.kun.client.annotation.HttpRequest;
import com.kun.client.annotation.MultiRequestBody;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author kun
 * @data 2022/1/16 15:10
 */
public class HttpRequestValidator implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Controller.class);
        if (Objects.isNull(beansWithAnnotation) || beansWithAnnotation.isEmpty()) {
            return;
        }
        beansWithAnnotation.forEach((beanName, bean) -> {
            Class<?> aClass = bean.getClass();
            Class<?>[] interfaces = aClass.getInterfaces();
            if (!httpRpcRequest(interfaces)) {
                return;
            }
            ReflectionUtils.doWithMethods(aClass, method -> validHttpRpcRequest(aClass, method), method -> method.isAnnotationPresent(RequestMapping.class));
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean httpRpcRequest(Class<?>[] interfaces) {
        if (Objects.isNull(interfaces) || interfaces.length == 0) {
            return false;
        }
        for (Class<?> anInterface : interfaces) {
            if (anInterface.isAnnotationPresent(HttpRequest.class)) {
                return true;
            }
            Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(anInterface);
            for (Method method : allDeclaredMethods) {
                if (method.isAnnotationPresent(HttpRequest.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validHttpRpcRequest(Class<?> aClass, Method method) {
        Parameter[] parameters = method.getParameters();
        Set<String> paramValueSet = new HashSet<>();
        for (Parameter parameter : parameters) {
            MultiRequestBody multiRequestBody = parameter.getAnnotation(MultiRequestBody.class);
            if (Objects.isNull(multiRequestBody)) {
                throw new IllegalStateException("class[" + aClass.getName() + "], method[" + method.getName() +
                        "] params must be annotated with @MultiRequestBody!");
            }
            boolean add = paramValueSet.add(multiRequestBody.value());
            if (!add) {
                throw new IllegalStateException("class[" + aClass.getName() + "], method[" + method.getName() +
                        "] params annotated with @MultiRequestBody must have different value!");
            }
        }
        Class<?>[] interfaces = aClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            try {
                Method methodOfInterface = anInterface.getMethod(method.getName(), method.getParameterTypes());
                String pathOfRequestMapping = getPathOfRequestMapping(aClass, method);
                String pathOfHttpRequest = getPathOfHttpRequest(anInterface, methodOfInterface);
                if (!pathOfRequestMapping.equals(pathOfHttpRequest)) {
                    throw new IllegalStateException("class[" + aClass.getName() + "], method[" + method.getName() +
                            "] @RequestMapping path must be same with its interface!");
                }
            } catch (NoSuchMethodException ignore){

            }
        }
    }

    private String getPathOfRequestMapping(Class<?> aClass, Method method) {
        RequestMapping annotationOfClass  = aClass.getAnnotation(RequestMapping.class);
        RequestMapping annotationOfMethod = method.getAnnotation(RequestMapping.class);
        String path = "";
        if (Objects.nonNull(annotationOfClass) && annotationOfClass.value().length > 0) {
            path = path + annotationOfClass.value()[0];
        }
        if (Objects.nonNull(annotationOfMethod) && annotationOfMethod.value().length > 0) {
            path = path + annotationOfClass.value()[0];
        }
        return path;
    }

    private String getPathOfHttpRequest(Class<?> aClass, Method method) {
        HttpRequest annotationOfClass  = aClass.getAnnotation(HttpRequest.class);
        HttpRequest annotationOfMethod = method.getAnnotation(HttpRequest.class);
        String path = "";
        if (Objects.nonNull(annotationOfClass)) {
            path = path + annotationOfClass.value();
        }
        if (Objects.nonNull(annotationOfMethod)) {
            path = path + annotationOfClass.value();
        }
        return path;
    }
}
