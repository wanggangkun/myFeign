package com.kun.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kun.client.annotation.HttpRequest;
import com.kun.client.annotation.MultiRequestBody;
import com.kun.core.model.HttpDomain;
import okhttp3.*;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * HttpConsumer注解代理类
 *
 * @author kun
 * @data 2022/1/15 19:30
 */
public class HttpConsumerInterceptor implements MethodInterceptor {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String HTTP_HEAD = "http://";

    private final Class<?> proxyKlass;

    private final HttpDomain httpDomain;

    private final OkHttpClient okHttpClient;

    public HttpConsumerInterceptor(Class<?> proxyKlass, HttpDomain httpDomain, OkHttpClient okHttpClient) {
        this.proxyKlass = proxyKlass;
        this.httpDomain = httpDomain;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        HttpRequest httpRequest = method.getAnnotation(HttpRequest.class);
        if (Objects.isNull(httpRequest)) {
            throw new IllegalStateException("method[" + method.getName() + "] must annotated with @HttpRequest!");
        }
        HttpRequest klassAnnotation = proxyKlass.getAnnotation(HttpRequest.class);
        String namespace = Objects.isNull(klassAnnotation) ? null : klassAnnotation.value();
        String url = getUrl(httpRequest.value(), namespace);
        Request request = buildRequest(method, args, url);
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody body = response.body();
        if (Objects.isNull(body)) {
            return null;
        }
        byte[] bytes = body.bytes();
        String res = new String(bytes, StandardCharsets.UTF_8);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(res, method.getReturnType());
        } catch (Throwable t) {
            Map<?, ?> map = OBJECT_MAPPER.readValue(res, Map.class);
            Object err = map.get("error");
            if (Objects.nonNull(err)) {
                throw new RuntimeException(err.toString());
            }
            throw new RuntimeException(t);
        }
    }

    private String getUrl(String requestPath, String namespace) {
        if (Objects.isNull(namespace)) {
            return HTTP_HEAD + httpDomain.getDomain() + ":" + httpDomain.getPort() + "/" + requestPath;
        }
        return HTTP_HEAD + httpDomain.getDomain() + ":" + httpDomain.getPort() + "/" + namespace + "/" + requestPath;
    }

    private Request buildRequest(Method method, Object[] args, String url) throws JsonProcessingException {
        Request.Builder builder = new Request.Builder();
        Map<String, Object> paramMap = new HashMap<>(4);
        Parameter[] parameters = method.getParameters();
        for (int i=0; i<parameters.length; i++) {
            Parameter parameter = parameters[i];
            MultiRequestBody multiRequestBody = parameter.getAnnotation(MultiRequestBody.class);
            if (Objects.isNull(multiRequestBody)) {
                throw new IllegalStateException("method[" + method.getName() + "] param must annotated with @MultiRequest!");
            }
            paramMap.put(multiRequestBody.value(), args[i]);
        }
        // 将参数构建为一个大JSON
        String param = OBJECT_MAPPER.writeValueAsString(paramMap);
        RequestBody requestBody = RequestBody.create(param, JSON);
        builder.post(requestBody);
        builder.url(url);
        return builder.build();
    }
}
