package com.kun.provider.client;

import com.kun.client.annotation.HttpRequest;
import com.kun.client.annotation.MultiRequestBody;

/**
 * @author kun
 * @data 2022/1/22 14:25
 */
@HttpRequest("demo")
public interface DemoHttpService {

    @HttpRequest("checkSuccess")
    String checkSuccess(@MultiRequestBody("param1") String param1, @MultiRequestBody("param2") String param2);
}
