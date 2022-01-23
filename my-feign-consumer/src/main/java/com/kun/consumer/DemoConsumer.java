package com.kun.consumer;

import com.kun.provider.client.DemoHttpService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kun
 * @data 2022/1/22 14:40
 */
@Component
public class DemoConsumer {
    @Resource
    private DemoHttpService demoHttpService;

    public String checkSuccess() {
        return demoHttpService.checkSuccess("param1", "param2");
    }
}
