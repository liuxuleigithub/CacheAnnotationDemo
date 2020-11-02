package com.lxl.cache.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * redis key
 *
 * @author jiachunhui
 * @version Id: CacheKeyConstant.java, v 0.1 2020/9/23 16:47 jiachunhui Exp $$
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheKeyConstant {

    /**
     * 字符 "."
     */
    public static final String POINT = ".";

    /**
     * 字符 "#"
     */
    public static final String HASH_TAG = "#";

    /**
     * 缓存 Key 前缀
     */
    public static final String UNITY_CERTIFY = "UNITY_CERTIFY_";

    /**
     * method get
     */
    public static final String GET = "get";

    /**
     * java.util.List
     */
    public static final String JAVA_UTIL_LIST = "java.util.List";
}