package com.lxl.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * CacheApplication
 *
 * @author liuxulei
 * @version Id: CacheApplication.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */
@EnableCaching
@SpringBootApplication
public class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }

}
