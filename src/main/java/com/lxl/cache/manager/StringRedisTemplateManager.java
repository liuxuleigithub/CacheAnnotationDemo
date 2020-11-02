package com.lxl.cache.manager;


import com.lxl.cache.constant.CacheKeyConstant;
import com.lxl.cache.utils.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存操作实现类
 *
 * @author liuxulei
 * @version Id: RedisCacheImpl.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */
@Log4j2
@Component
public class StringRedisTemplateManager {


    /**
     * redis模板
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 缓存数据，永久保存
     *
     * @param key   key
     * @param value value
     * @return 是否成功
     */
    public boolean setObject(String key, Object value) {
        return setObject(CacheKeyConstant.UNITY_CERTIFY + key, value, 0L, TimeUnit.MINUTES);
    }

    /**
     * 缓存数据,有过期时间(自定义)
     *
     * @param key     key
     * @param value   value
     * @param timeout 时间
     * @return boolean
     */
    public boolean setObject(final String key, final Object value, final long timeout,
                             final TimeUnit timeUnit) {
        final String jsonValue = toJson(value);
        if (timeout > 0) {
            redisTemplate.boundValueOps(CacheKeyConstant.UNITY_CERTIFY + key).set(jsonValue, timeout, timeUnit);
        } else {
            redisTemplate.boundValueOps(CacheKeyConstant.UNITY_CERTIFY + key).set(jsonValue);
        }
        return true;
    }

    /**
     * 获取缓存对象
     *
     * @param key 查询关键字
     * @return <T> T
     */
    public <T> T get(final String key, final Class<T> clazz) {
        try {
            return JsonUtils.fromJson(redisTemplate.boundValueOps(
                    CacheKeyConstant.UNITY_CERTIFY + key).get(), clazz);
        } catch (Exception e) {
            log.info("Redis处理异常:{},{}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存list
     *
     * @param key   key
     * @param clazz class
     * @param <T>   t
     * @return List
     */
    public <T> List<T> getList(final String key, final Class<T> clazz) {
        try {
            return JsonUtils.jsonToList(redisTemplate.boundValueOps(
                    CacheKeyConstant.UNITY_CERTIFY + key).get(), clazz);
        } catch (Exception e) {
            log.error("Redis处理异常: {}", e);
        }
        return null;
    }

    /**
     * 清除缓存
     *
     * @param key key
     */
    public boolean delete(String key) {
        redisTemplate.delete(CacheKeyConstant.UNITY_CERTIFY + key);
        return true;
    }

    /**
     * redis 值如果是对象转成json,如果是字符串不变
     *
     * @param value 值
     * @return 值
     */
    private String toJson(Object value) {

        if (value instanceof String) {
            return (String) value;
        }
        return JsonUtils.toJson(value);
    }

}
