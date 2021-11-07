package com.shanjupay.common.cache;

import java.util.Set;

public interface Cache {

    /**
     * 列出所有的 key
     */
    Set<String> getKeys();

    Set<String> getKeys(String pattern);

    /**
     * 检查 key 是否存在
     *
     * @param key
     * @return
     */
    Boolean exists(String key);

    /**
     * 移除给定的一个或多个 key, 如果 key 不存在, 则忽略该命令
     */
    void del(String key);

    /**
     * 简单的字符串设置
     *
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * 简单的字符串设置
     *
     * @param key
     * @param value
     */
    void set(String key, String value, Integer expiration);

    /**
     * 返回 key 所关联的字符串值
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * key seconds 为给定 key 设置生存时间, 当 key 过期时, 它会被自动删除
     *
     * @param key
     * @param expire
     */
    void expire(String key, int expire);

    /**
     * 如果 key 已经存在并且是一个字符串, append 命令将 value 追加到 key 原来的值之后
     *
     * @param key
     * @param value
     */
    void append(String key, String value);

    /**
     * 获取旧值返回新值, 不存在返回 null
     *
     * @param key
     * @param newValue
     * @return 旧值
     */
    String getset(String key, String newValue);

    /**
     * 分布锁
     *
     * @param key
     * @param value
     * @return
     */
    boolean setnx(String key, String value);

    /**
     * 计数器
     */
    Long incrBy(String key, Long delta);
}
