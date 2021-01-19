package com.coder.distributed.lock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author LGZ
 * @package com.coder.distributed.lock.config
 * @className JedisUtil
 * @description distributed-lock JedisUtil
 * @date 2021/1/19 15:26:00
 */
@Component
public class JedisUtil {

    @Value("${spring.redis.database}")
    private Integer database;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdele;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdele;

    private static JedisPool jedisPool;

    private void instance() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxIdle(maxIdele);
        jedisPool = new JedisPool(jedisPoolConfig, host, Integer.valueOf(port));
    }

    public Jedis getJedis() {
        if (jedisPool == null) {
            instance();
        }
        return jedisPool.getResource();
    }


}
