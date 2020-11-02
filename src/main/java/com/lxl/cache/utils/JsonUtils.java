package com.lxl.cache.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * @author liuxulei
 * @version Id: JsonUtils.java, v 0.1 2020/11/2 6:09 下午 liuxulei Exp $$
 */
public final class JsonUtils {

    /**
     * 私有构建函数
     */
    private JsonUtils() {
    }

    /**
     * 初始化Gson
     */
    private static Gson gson = new Gson();

    /**
     * 序列化json
     *
     * @param json json字符串
     * @param cls  类
     * @param <T>  泛型
     * @return 结果
     */
    public static <T> T fromJson(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    /**
     * json字符串转成list
     *
     * @param jsonString
     * @param cls
     * @return List结果
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(jsonString, new ParameterizedTypeImpl(cls));
        }
        return list;
    }


    /**
     * json字符串转成map的
     *
     * @param jsonString
     * @return Map结果
     */
    public static <T> Map<String, T> jsonToMaps(String jsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(jsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }


    /**
     * 反序列化json
     *
     * @param object
     * @return json
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }


    /**
     * gson 转list(泛型)
     *
     * @author liuxulei
     * @version Id: ParameterizedTypeImpl.java, v 0.1 2020/10/12 9:23 AM liuxulei Exp $$
     */
    private static class ParameterizedTypeImpl implements ParameterizedType {

        Class clazz;

        ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    /**
     * 序列化json
     *
     * @param object
     * @return json
     * @author chengkuo
     */
    public static String toJsonIngoreNulValue(Object object) {
        gson.newBuilder().serializeNulls();
        return gson.toJson(object);
    }
}
