package com.coder.distributed.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LGZ
 * @package PACKAGE_NAME
 * @className com.coder.distributed.lock.controller.RedisController
 * @description distributed-lock com.coder.distributed.lock.controller.RedisController
 * @date 2021/1/18 13:28:52
 */
@Slf4j
@RestController
public class RedisController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    private String serverPort;

    private final Lock lock = new ReentrantLock();

    @GetMapping("buy")
    public synchronized String buy() {
        String key = "Mi11";
        String returnString;
        for (; ; ) {
            boolean lockResult = lock.tryLock();
            if (lockResult) {
                try {
                    //查看库存
                    String result = stringRedisTemplate.opsForValue().get(key);
                    int number = result == null ? 0 : Integer.parseInt(result);
                    if (number > 0) {
                        int realNumber = number - 1;
                        stringRedisTemplate.opsForValue().set(key, String.valueOf(realNumber));
                        returnString = "服务提供商：" + serverPort + "\t" + key + " 库存-1,剩余数量：" + realNumber;
                        log.info(returnString);
                    } else {
                        returnString = "服务提供商：" + serverPort + "\t" + key + " 商品数量不足";
                        log.info(returnString);
                    }
                    lock.unlock();
                    return returnString;
                } catch (Exception e) {
                    lock.unlock();
                    return null;
                }
            }
        }

    }
}
