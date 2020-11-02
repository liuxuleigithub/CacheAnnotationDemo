package com.lxl.cache.annotation;


import java.lang.annotation.*;

/**
 * redis缓存批量删除注解
 *
 * @author liuxulei
 * @version Id: Caching.java, v 0.1 2020/10/15 3:37 PM liuxulei Exp $$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caching {

    //支持多个CacheEvict数组
    CacheEvict[] evict() default {};

}
