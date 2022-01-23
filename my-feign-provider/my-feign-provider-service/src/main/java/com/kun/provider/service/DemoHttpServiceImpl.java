package com.kun.provider.service;

import com.alibaba.fastjson.JSON;
import com.kun.client.annotation.MultiRequestBody;
import com.kun.provider.client.DemoHttpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kun
 * @data 2022/1/22 14:31
 */
@RestController
@RequestMapping("demo")
public class DemoHttpServiceImpl implements DemoHttpService {

    @Override
    @RequestMapping("checkSuccess")
    @ResponseBody
    public String checkSuccess(@MultiRequestBody("param1") String param1, @MultiRequestBody("param2") String param2) {
        System.out.println("checkSuccess#" + param1 + "#" +param2);
        return JSON.toJSONString("checkSuccess#" + param1 + "#" +param2);
    }
}
