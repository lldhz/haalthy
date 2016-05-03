package com.haalthy.service.cache;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ken-Asus on 2016/5/1.
 */
@Service("redisSortedSetCache")
public class RedisSortedSetCache {
    protected Logger logger=Logger.getLogger(this.getClass());

    @SuppressWarnings("rawtypes")
    @Resource
    private RedisTemplate redisTemplate;
    private static final String namespace="haalthy.sortedset.";

    @SuppressWarnings("unchecked")
    public void putValue(String strKey,String strMem,long lScore)
    {

        logger.info("putObject to redis data:" + strKey+strMem+lScore);
        redisTemplate.execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] btyKey = redisTemplate.getStringSerializer().serialize(namespace + strKey);
                byte[] btyField = redisTemplate.getStringSerializer().serialize(strMem);
                connection.zAdd(btyKey,lScore,btyField);
                return null;
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void incrValue(String strKey,String strMem,long lScore)
    {

        logger.info("putObject to redis data:" + strKey+strMem+lScore);
        redisTemplate.execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] btyKey = redisTemplate.getStringSerializer().serialize(namespace + strKey);
                byte[] btyField = redisTemplate.getStringSerializer().serialize(strMem);
                connection.zIncrBy(btyKey,lScore,btyField);
                return null;
            }
        });

    }

    @SuppressWarnings("unchecked")
    public Map<String,Long> getValues(String strKey,long start,long end)
    {

        logger.info("getObjects to redis data:" + strKey+start+end);
        Map<String,Long> resultMap = new HashMap<String,Long>();

        Set<RedisZSetCommands.Tuple> set = (Set<RedisZSetCommands.Tuple>) redisTemplate.execute(new RedisCallback<Set<RedisZSetCommands.Tuple>>() {

            @Override
            public Set<RedisZSetCommands.Tuple>doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] btyKey = redisTemplate.getStringSerializer().serialize(namespace + strKey);
                
                return connection.zRangeWithScores(btyKey,start,end);
            }
        });

        for (RedisZSetCommands.Tuple tuple: set
             ) {
            resultMap.put(tuple.getValue().toString(),tuple.getScore().longValue());
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    public void delValue(String strKey,String strMem)
    {
        logger.info("del Object to redis data:" + strKey+strMem);
        redisTemplate.execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] btyKey = redisTemplate.getStringSerializer().serialize(namespace + strKey);
                byte[] btyField = redisTemplate.getStringSerializer().serialize(strMem);
                connection.zRem(btyKey,btyField);
                return null;
            }
        });
    }

}
