package com.kun.starter;

import com.kun.core.HttpConsumerPostProcessor;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 服务调用方生效
 *
 * @author kun
 * @data 2022/1/16 15:36
 */
@ConditionalOnClass(OkHttpClient.class)
public class HttpClientAuthConfig {

    @Bean
    @ConditionalOnMissingBean
    public HttpConsumerPostProcessor httpConsumerPostProcessor() {
        return new HttpConsumerPostProcessor();
    }
}
