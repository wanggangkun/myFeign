package com.kun.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author kun
 * @data 2022/1/22 14:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ConsumerTest {

    @Resource
    DemoConsumer demoConsumer;

    @Test
    public void checkSuccess() {
        System.out.println(demoConsumer.checkSuccess());
    }

}
