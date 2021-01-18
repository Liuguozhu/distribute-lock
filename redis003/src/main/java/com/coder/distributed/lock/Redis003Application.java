package com.coder.distributed.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LGZ
 * @package com.coder.distributed.lock.config
 * @className Redis001Application
 * @description distributed-lock Redis001Application
 * @date 2021/1/18 13:33:51
 */

@Slf4j
@SpringBootApplication
@ComponentScan("com.coder.distributed.lock")
public class Redis003Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis003Application.class, args);

        log.info(Redis003Application.class.getSimpleName() + " is success!");
    }
}

