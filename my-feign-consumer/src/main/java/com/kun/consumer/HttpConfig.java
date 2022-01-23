package com.kun.consumer;

import com.kun.client.annotation.HttpConsumer;
import com.kun.provider.client.DemoHttpService;
import org.springframework.context.annotation.Configuration;

/**
 * @author kun
 * @data 2022/1/22 14:37
 */
@Configuration
public class HttpConfig {

    @HttpConsumer(domain = "localhost", port = "8080")
    private DemoHttpService demoHttpService;
}
