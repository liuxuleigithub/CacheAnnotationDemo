package com.lxl.cache.annotation;

import java.lang.annotation.*;

/**
 * redis缓存删除注解
 *
 * @author liuxulei
 * @version Id: CacheEvict.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    //缓存的名字，配合key一起使用
    String cacheName() default "cacheName";

    //数组key 使用数组中的key进行拼接
    String[] key();

}
