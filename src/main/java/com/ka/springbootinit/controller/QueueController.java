package com.ka.springbootinit.controller;


import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "local"})
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(()->{
            log.info("Task Running " + name + ", by " + Thread.currentThread().getName());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, threadPoolExecutor);
    }
    @GetMapping("/get")
    public String get() {
        Map<String,Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("Queue Size",size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("Task Count",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("Completed Task Count", completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("Active Count",activeCount);
        return JSONUtil.toJsonStr(map);
    }
}
