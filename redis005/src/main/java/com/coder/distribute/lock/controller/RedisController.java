package com.coder.distribute.lock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
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
 * @className com.coder.distributed.com.coder.distribute.lock.controller.RedisController
 * @description distributed-com.coder.distribute.lock com.coder.distributed.com.coder.distribute.lock.controller.RedisController
 * @date 2021/1/18 13:28:52
 */
@Slf4j
@RestController
public class RedisController {

    private static final String REDIS_LOCK = "DISTRIBUTE_LOCK";

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Redisson redisson;

    @GetMapping("buy")
    public String buy() {
        String key = "Mi11";
        String returnString;
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
       // 获取锁
        RLock lock = redisson.getLock(REDIS_LOCK);
        try {
            // 加锁，同时给增加过期时间，防止程序宕机一直没有释放锁。
            boolean lockResult = lock.tryLock(10, TimeUnit.SECONDS);
            if (lockResult) {
                log.info(value + "加锁成功！");

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

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
            log.info(value + "释放锁成功！");
        }
        return null;
    }


}
