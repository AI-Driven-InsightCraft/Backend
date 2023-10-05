package com.ka.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class RedisLimiterManagerTest {
    @Resource
    private RedisLimiterManager redisLimiterManager;


    /*
     * Basic Test on Redis connection and how Limiter work
     */
    @Test
    void doRateTest() throws InterruptedException{
        String userId = "1";
        for(int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("good1");
        }
        Thread.sleep(2000);
        for(int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("good2");
        }

    }

}