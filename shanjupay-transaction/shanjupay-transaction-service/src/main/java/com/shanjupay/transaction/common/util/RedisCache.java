package com.shanjupay.transaction.common.util;

import com.shanjupay.common.cache.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public class RedisCache implements Cache {

    private StringRedisTemplate redisTemplate;

    public RedisCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 列出所有的 key
     */
    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return null;
    }

    /**
     * 检查 key 是否存在
     *
     * @param key
     * @return
     */
    @Override
    public Boolean exists(String key) {
        return null;
    }

    /**
     * 移除给定的一个或多个 key, 如果 key 不存在, 则忽略该命令
     *
     * @param key
     */
    @Override
    public void del(String key) {

    }

    /**
     * 简单的字符串设置
     *
     * @param key
     * @param value
     * @param expiration
     */
    @Override
    public void set(String key, String value, Integer expiration) {

    }

    /**
     * 返回 key 所关联的字符串值
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        return null;
    }

    /**
     * key seconds 为给定 key 设置生存时间, 当 key 过期时, 它会被自动删除
     *
     * @param key
     * @param expire
     */
    @Override
    public void expire(String key, int expire) {

    }

    /**
     * 如果 key 已经存在并且是一个字符串, append 命令将 value 追加到 key 原来的值之后
     *
     * @param key
     * @param value
     */
    @Override
    public void append(String key, String value) {

    }

    /**
     * 获取旧值返回新值, 不存在返回 null
     *
     * @param key
     * @param newValue
     * @return 旧值
     */
    @Override
    public String getset(String key, String newValue) {
        return null;
    }

    /**
     * 分布锁
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean setnx(String key, String value) {
        return false;
    }

    /**
     * 计数器
     *
     * @param key
     * @param delta
     */
    @Override
    public Long incrBy(String key, Long delta) {
        return null;
    }
}
