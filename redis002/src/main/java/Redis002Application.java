import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LGZ
 * @package com.coder.distributed.com.coder.distribute.lock.config
 * @className Redis001Application
 * @description distributed-com.coder.distribute.lock Redis001Application
 * @date 2021/1/18 13:33:51
 */

@Slf4j
@SpringBootApplication
@ComponentScan("com.coder.distributed")
public class Redis002Application {

    public static void main(String[] args) {
        SpringApplication.run(Redis002Application.class, args);

        log.info(Redis002Application.class.getSimpleName() + " is success!");
    }
}

