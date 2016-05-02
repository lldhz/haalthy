package com.haalthy.service.cache;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * Created by Ken-Asus on 2016/5/1.
 */
public class RedisSortedSetCache {
    protected Logger logger=Logger.getLogger(this.getClass());

    @SuppressWarnings("rawtypes")
    @Resource
    private RedisTemplate redisTemplate;
    private static final String namespace="haalthy.sortedset.";
}
