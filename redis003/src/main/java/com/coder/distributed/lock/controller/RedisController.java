package com.coder.distributed.lock.controller;

import com.coder.distributed.lock.config.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Collections;
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

    /**
     * RedisTemplate 的子类,子类一般比父类的功能更多。
     **/
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JedisUtil jedisUtil;

    @GetMapping("buy")
    public String buy() {
        String key = "Mi11";
        String returnString = null;
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        for (; ; ) {
            // 加锁，相当于 redis 命令的 SETNX 同时给key增加过期时间，防止程序宕机一直没有释放锁。
            Boolean lockResult = stringRedisTemplate.opsForValue().setIfAbsent(REDIS_LOCK, value, 10L, TimeUnit.SECONDS);
            if (lockResult == null || lockResult) {
                log.info(value + "加锁成功！");
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
//                    // 无事务--无法保证 if 判断 和 delete 的原子性
//                    // 验证是不是自己的锁，防止程序中途卡住，消耗时间超过10秒，锁已被 redis 过期释放。别的线程已经抢到了新的锁，避免删除了别人的锁。造成恶性循环。
//                    if (value.equals(stringRedisTemplate.opsForValue().get(REDIS_LOCK))) {
//                        // 无论中间程序有无出现异常，都必须要释放锁
//                        stringRedisTemplate.delete(REDIS_LOCK);
//                    }
                    //redis 官方建议是通过 lua 脚本保证原子性 详情参看：https://redis.io/commands/set。
                    //由于reidTemplate 实现script 脚本比较复杂，所以我们使用 jedis 来实现
                    Jedis jedis = jedisUtil.getJedis();
                    String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                            "then " +
                            "    return redis.call('del',KEYS[1]) " +
                            "else " +
                            "    return 0 " +
                            "end";
                    try {
                        Object o = jedis.eval(script, Collections.singletonList(REDIS_LOCK), Collections.singletonList(value));
                        if ("1".equals(o.toString())) {
                            log.info(" lua 脚本 redis锁释放成功");
                        } else {
                            log.info(" lua 脚本 redis锁释放失败");
                        }
                    } catch (Exception e) {
                        log.info(" lua 脚本 redis锁释放异常：{}", e);
                    } finally {
                        if (null != jedis) {
                            jedis.close();
                        }
                    }

//                    // 如果不用lua脚本，我们可以使用 redis 事务来保证 if 和 delete 的原子性
//                    // redis 事务说明(https://github.com/Liuguozhu/distribute-lock/blob/master/Redis-Transactions.md)
//                    while (true) {
//                        log.info(value + "开始设置事务");
//                        stringRedisTemplate.watch(REDIS_LOCK);
//                        if (value.equals(stringRedisTemplate.opsForValue().get(REDIS_LOCK))) {
//                            stringRedisTemplate.multi();
//                            // 无论中间程序有无出现异常，都必须要释放锁
//                            stringRedisTemplate.delete(REDIS_LOCK);
//                            List<Object> list = stringRedisTemplate.exec();
//                            if (list == null) {
//                                log.info(value + "事务执行失败");
//                                stringRedisTemplate.discard();
//                                continue;
//                            }
//                        } else {
//                            log.info(value + "校验锁不一致······");
//                        }
//                        log.info(value + "事务执行成功");
//                        stringRedisTemplate.unwatch();
//                        log.info(value + "释放锁成功！");
//                        break;
//                    }

                }
            }
        }
    }


}
