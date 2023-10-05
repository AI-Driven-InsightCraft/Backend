package com.ka.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIManagerTest {

    @Resource
    private AIManager aiManager;
    @Test
    void doChat() {
        String answer = aiManager.doChat(1651468516836098050L,"周杰伦");
        System.out.println(answer);
    }
}