package com.lxl.cache.annotation;


import java.lang.annotation.*;

/**
 * redis缓存存储注解
 *
 * @author liuxulei
 * @version Id: CacheAble.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheAble {

    //缓存的名字，配合key一起使用
    String cacheName() default "cacheName";

    //key，传入的对象 #id
    String[] key();

    //设置键的存活时间。默认0 永久。单位秒
    long time() default 0;


}
