package com.haalthy.service.openservice;

import com.haalthy.service.cache.RedisMapCache;
import com.haalthy.service.cache.RedisSortedSetCache;
import com.haalthy.service.domain.Knowledges;
import com.haalthy.service.persistence.KnowledgesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ken on 3/5/16.
 */
@Service
public class KnowledgesService {

    @Autowired
    private RedisSortedSetCache redisSortedSetCache;

    @Autowired
    private RedisMapCache redisMapCache;

    @Autowired
    private KnowledgesMapper knowledgesMapper;

    private static String hotKeywords = "SearchKeywords";
    private static String keywordUuid = "Keyword.";

    public Map<String,Long> getHotKeywords()
    {
        return redisSortedSetCache.getValues(hotKeywords,0,15);
    }

    public void setHotKeywords(String keywords)
    {
        redisSortedSetCache.incrValue(hotKeywords,keywords,1);
    }

    public void setKeywordUuid(String keyword,String uuid)
    {
        redisMapCache.setValue(keywordUuid+keyword,uuid,String.valueOf(System.currentTimeMillis()),0);
    }

    public void setKeywordUuid(String keyword,List<String> uuids)
    {
        for (String uuid:uuids
             ) {
            redisMapCache.setValue(keywordUuid+keyword,uuid,String.valueOf(System.currentTimeMillis()),0);
        }
    }

    public List<String> getKeywordUuid(String keyword)
    {
        return redisMapCache.getAllFields(keywordUuid+keyword);
    }

    public List<String> getKeywordsUuid(List<String> keywords)
    {
        List<String> resultList = new ArrayList<String>();
        for(String keyword:keywords)
        {
            List<String> temp = redisMapCache.getAllFields(keywordUuid+keyword);
            resultList.removeAll(temp);
            resultList.addAll(temp);
        }
        return resultList;
    }

    public List<Knowledges> getKnowledgesByKeyword(String keyword)
    {
        return knowledgesMapper.selectKnowledgesKeywords(keyword);
    }

    public List<Knowledges> getKnowledgesByKeyword(List<String> keyword)
    {
        List<Knowledges> resultList = new ArrayList<Knowledges>();
        for (String key:keyword
             ) {
            List<Knowledges> temp = knowledgesMapper.selectKnowledgesKeywords(key);
            resultList.removeAll(temp);
            resultList.addAll(temp);
        }
        return resultList;
    }
}
