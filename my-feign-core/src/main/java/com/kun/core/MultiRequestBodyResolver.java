package com.kun.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kun.client.annotation.MultiRequestBody;
import com.kun.core.util.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

/**
 * 参数解析
 *
 * @author kun
 * @data 2022/1/15 21:06
 */
public class MultiRequestBodyResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String requestBody = getRequestBody(nativeWebRequest);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JsonNode rootNode = OBJECT_MAPPER.readTree(requestBody);
        if (StringUtils.isEmpty(rootNode)) {
            throw new IllegalArgumentException("requestBody must not empty!");
        }
        MultiRequestBody multiRequestBody = methodParameter.getParameterAnnotation(MultiRequestBody.class);
        if (Objects.isNull(multiRequestBody)) {
            throw new IllegalArgumentException("param must annotated with @MultiRequestBody!");
        }
        String key = !StringUtils.isEmpty(multiRequestBody.value()) ? multiRequestBody.value() : methodParameter.getParameterName();
        JsonNode value = rootNode.get(key);
        if (Objects.isNull(value)) {
            return null;
        }
        Class<?> paramType = methodParameter.getParameterType();
        return OBJECT_MAPPER.readValue(value.toString(), paramType);
    }

    /**
     *
     * 获取请求体的JSON字符串
     */
    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) webRequest.getAttribute(JSON_REQUEST_BODY, NativeWebRequest.SCOPE_REQUEST);
        if (!StringUtils.isEmpty(jsonBody)) {
            return jsonBody;
        }
        try (BufferedReader reader = servletRequest.getReader()) {
            jsonBody = IOUtils.toString(reader);
            webRequest.setAttribute(JSON_REQUEST_BODY, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            return jsonBody;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
