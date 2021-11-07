package com.shanjupay.transaction.common.util;

import com.shanjupay.common.cache.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        return getKeys("");
    }

    @Override
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 检查 key 是否存在
     *
     * @param key
     * @return
     */
    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 移除给定的一个或多个 key, 如果 key 不存在, 则忽略该命令
     *
     * @param key
     */
    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 简单的字符串设置
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
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
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.SECONDS);
    }

    /**
     * 返回 key 所关联的字符串值
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * key seconds 为给定 key 设置生存时间, 当 key 过期时, 它会被自动删除
     *
     * @param key
     * @param expire
     */
    @Override
    public void expire(String key, int expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 如果 key 已经存在并且是一个字符串, append 命令将 value 追加到 key 原来的值之后
     *
     * @param key
     * @param value
     */
    @Override
    public void append(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
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
        return redisTemplate.opsForValue().getAndSet(key, newValue);
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
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 计数器
     *
     * @param key
     * @param delta
     */
    @Override
    public Long incrBy(String key, Long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
