package com.lxl.cache.aop;

import com.lxl.cache.constant.CacheKeyConstant;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 缓存注解辅助类
 *
 * @author liuxulei
 * @version Id: CacheAspectHelper.java, v 0.1 2020/10/10 9:23 AM liuxulei Exp $$
 */
@Log4j2
@Component
public class CacheAspectHelper {

    /**
     * 获取key值
     *
     * @param joinPoint 切入点
     * @param keyInput  key值
     * @param cacheName 缓存空间名
     */
    public String getCacheKey(ProceedingJoinPoint joinPoint, String[] keyInput, String cacheName) throws Exception {

        if (!checkKey(keyInput)) {
            log.error("CacheKey input error {}", Arrays.toString(keyInput));
        }
        String cacheKey = cacheName;
        for (String s : keyInput) {
            String key = getSubstringKey(s);
            //判定是否有. 例如#user.id  有则要处理，无则进一步处理
            if (!key.contains(CacheKeyConstant.POINT)) {
                Object arg = getArg(joinPoint, key);
                //判定请求参数中是否有相关参数。无则直接当键处理，有则取值当键处理
                cacheKey = arg == null ? cacheKey + key : cacheKey + arg;
            } else {
                //拿到对象参数 例如  user.id  拿到的是user这个相关对象
                Object arg = getArg(joinPoint, handlerIncludeSpot(key));
                Object objectKey = getObjectKey(arg, key.substring(key.indexOf(CacheKeyConstant.POINT) + 1));
                cacheKey = cacheKey + objectKey;
            }

        }
        return cacheKey;
    }

    /**
     * 递归找到相关的参数，并最终返回一个值
     *
     * @param object 传入的对象
     * @param key    key名,用于拼接成 get+key
     * @return 返回处理后拿到的值  比如 user.id  id的值是10  则将10返回
     * @throws Exception 异常
     */
    private Object getObjectKey(Object object, String key) throws Exception {
        //判断key是否为空
        if (StringUtils.isEmpty(key)) {
            return object;
        }
        //拿到user.xxx  例如：key是user.user.id  递归取到最后的id。并返回数值
        int doIndex = key.indexOf(CacheKeyConstant.POINT);
        if (doIndex > 0) {
            String propertyName = key.substring(0, doIndex);
            //截取
            key = key.substring(doIndex + 1);
            Object obj = getProperty(object, getMethod(propertyName));
            return getObjectKey(obj, key);
        }
        return getProperty(object, getMethod(key));
    }

    /**
     * 截取key
     *
     * @param key key值
     */
    private String handlerIncludeSpot(String key) {
        int doIndex = key.indexOf(CacheKeyConstant.POINT);
        return key.substring(0, doIndex);
    }

    /**
     * 获取某方法中的返回值
     *
     * @param object     对象实例
     * @param methodName 方法名
     */
    private Object getProperty(Object object, String methodName) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        return object == null ? null : object.getClass().getMethod(methodName).invoke(object);
    }

    /**
     * 返回截取的的字符串
     *
     * @param keys 用于截取的键
     * @return 返回截取的的字符串
     */
    private String getSubstringKey(String keys) {
        //去掉# ，在设置例如 #user 变成 user
        return keys.substring(1).substring(0, 1) + keys.substring(2);
    }

    /**
     * 获得bean字段对应的get方法
     *
     * @param key 字段名，用于拼接
     * @return 方法名字（即getXXX() ）
     */
    private String getMethod(String key) {

        return CacheKeyConstant.GET + Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

    /**
     * 获取请求的参数。
     *
     * @param joinPoint 切点
     * @param paramName 请求参数的名字
     * @return 返回和参数名一样的参数对象或值
     * @throws NoSuchMethodException 异常
     */
    private Object getArg(ProceedingJoinPoint joinPoint, String paramName) throws NoSuchMethodException {
        Signature signature = joinPoint.getSignature();
        //获取请求的参数
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method0 = joinPoint.getTarget().getClass()
                .getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] p = parameterNameDiscoverer.getParameterNames(method0);
        if (p == null) {
            log.error("没有参数 {}", paramName + "没有方法:{}", method0);
        }
        //判断是否有相关参数
        int index = 0;
        for (String string : p) {
            if (string.equalsIgnoreCase(paramName)) {
                return joinPoint.getArgs()[index];
            }
            index++;
        }
        return null;
    }

    /**
     * 键规则检验 是否符合开头#
     *
     * @param key 传入的key
     * @return 返回是否包含
     */
    private Boolean checkKey(String[] key) {
        Boolean check = Boolean.FALSE;
        if (key == null || key.length == 0) {
            return Boolean.FALSE;
        }
        for (String aKey : key) {
            String temp = aKey.substring(0, 1);
            //如果没有以#开头，报错
            if (temp.equals(CacheKeyConstant.HASH_TAG)) {
                check = Boolean.TRUE;
            }
        }
        return check;
    }

    /**
     * 判断参数类型是否为list
     *
     * @param joinPoint 切入点
     * @return boolean
     */
    public Boolean isList(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Class declaringType = signature.getDeclaringType();
        String name = signature.getName();
        Method[] methods = declaringType.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                //判断类型
                return CacheKeyConstant.JAVA_UTIL_LIST.equals(method.getReturnType().getName())
                        ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return false;
    }


    /**
     * 支持对象，基本类型，引用类型 集合
     *
     * @param joinPoint 切入点
     * @return class
     */
    public Class<?> getMethodReturnType(ProceedingJoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();
        Class declaringType = signature.getDeclaringType();
        String name = signature.getName();
        Method[] methods = declaringType.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                //如果缓存类型为list 取入参类型返回 用于json转换中clazz
                return CacheKeyConstant.JAVA_UTIL_LIST.equals(method.getReturnType().getName())
                        ? method.getParameterTypes()[0] : method.getReturnType();
            }

        }
        return null;
    }

}