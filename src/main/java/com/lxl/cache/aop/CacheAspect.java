package com.lxl.cache.aop;

import com.lxl.cache.annotation.CacheAble;
import com.lxl.cache.annotation.CacheEvict;
import com.lxl.cache.annotation.Caching;
import com.lxl.cache.manager.StringRedisTemplateManager;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis缓存注解切面
 *
 * @author liuxulei
 * @version Id: CacheAspect.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */
@Log4j2
@Aspect
@Component
public class CacheAspect {

    /**
     * redis缓存辅助类
     */
    @Autowired
    CacheAspectHelper cacheAspectHelper;

    /**
     * redis缓存辅助类
     */
    @Autowired
    StringRedisTemplateManager stringRedisTemplateManager;

    /**
     * redis缓存存储切入
     *
     * @param joinPoint 切点
     * @param cacheAble 注解
     */
    @Around("@annotation(cacheAble)")
    public Object handlers(ProceedingJoinPoint joinPoint, CacheAble cacheAble) {
        try {
            Object cacheValue;
            //拿到返回值类型
            Class<?> methodReturnType = cacheAspectHelper.getMethodReturnType(joinPoint);
            //拿到存入redis的键
            String cacheKey = cacheAspectHelper.getCacheKey(joinPoint, cacheAble.key(), cacheAble.cacheName());
            //判断是否为list
            if (cacheAspectHelper.isList(joinPoint)) {
                //处理从redis拿出的字符串。
                cacheValue = stringRedisTemplateManager.getList(cacheKey, methodReturnType);
            } else {
                cacheValue = stringRedisTemplateManager.get(cacheKey, methodReturnType);
            }
            //如果有缓存则返回缓存的值
            if (cacheValue != null) {
                log.info("使用缓存key :{}", cacheKey);
                //处理从redis拿出的字符串。
                return cacheValue;
            }
            //执行原来方法
            Object proceed = joinPoint.proceed();
            //放入缓存
            stringRedisTemplateManager.setObject(cacheKey, proceed, cacheAble.time(), TimeUnit.MINUTES);
            return proceed;
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    /**
     * redis缓存删除切入
     *
     * @param joinPoint  切点
     * @param cacheEvict 注解
     */
    @Around("@annotation(cacheEvict)")
    public Object handlers(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) {
        Object object = null;
        try {
            object = joinPoint.proceed();
            //移除缓存
            removeCacheKey(cacheEvict, joinPoint);
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return object;
    }

    /**
     * 缓存删除多个 cacheEvict
     *
     * @param joinPoint 切点
     * @param caching   注解
     */
    @Around("@annotation(caching)")
    public Object handlers(ProceedingJoinPoint joinPoint, Caching caching) {

        Object object = null;
        try {
            object = joinPoint.proceed();
            for (int i = 0; i < caching.evict().length; i++) {
                removeCacheKey(caching.evict()[i], joinPoint);
            }
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return object;
    }


    /**
     * 移除缓存
     *
     * @param cacheEvict 注解
     * @param joinPoint  切点
     */
    private void removeCacheKey(CacheEvict cacheEvict, ProceedingJoinPoint joinPoint) throws Exception {

        String cacheKey = cacheAspectHelper.getCacheKey
                (joinPoint, cacheEvict.key(), cacheEvict.cacheName());
        log.info("删除缓存key {}", cacheKey);
        stringRedisTemplateManager.delete(cacheKey);
    }


}