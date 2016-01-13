package com.haalthy.service.common;

import org.apache.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.security.Provider;
import java.util.Properties;

/**
 * Created by Ken on 2015/12/18.
 */

public class ConfigLoader {

    private static Logger logger = Logger.getLogger(ConfigLoader.class);

    private static ConfigLoader configLoader=null;
    private Properties redisProps;
    private Provider props;

    private ConfigLoader(){
        try {
            redisProps = PropertiesLoaderUtils.loadAllProperties("redis.properties");
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            logger.error("load chartServer Config failed!",e);
        }
    }

    public synchronized static ConfigLoader getInstance(){
        if(configLoader==null){
            configLoader=new ConfigLoader();
        }
        return configLoader;
    }

    public String getProperty(String key){
        return this.props.getProperty(key);
    }

    public String getRedisProperty(String key){
        return this.redisProps.getProperty(key);
    }

}