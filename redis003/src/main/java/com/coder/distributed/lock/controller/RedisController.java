package com.coder.distributed.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private static final String REDIS_LOCK = "DISTRIBUTE_LOCK";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    private String serverPort;


    @GetMapping("buy")
    public String buy() {
        String key = "Mi11";
        String returnString = null;
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        for (; ; ) {
            // 加锁，相当于 redis 命令的 SETNX 同时给key增加过期时间，防止程序宕机一直没有释放锁。
            Boolean lockResult = stringRedisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, value, 10L, TimeUnit.SECONDS);
            if (lockResult == null || lockResult) {
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
                    return returnString;
                } catch (Exception e) {
                    e.printStackTrace();
                    return returnString;
                } finally {
                    if (value.equals(stringRedisTemplate.opsForValue().get(REDIS_LOCK))) {
                        // 无论中间程序有无出现异常，都必须要释放锁
                        stringRedisTemplate.delete(REDIS_LOCK);
                    }
                }
            }
        }
    }


}
