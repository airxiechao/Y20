package com.airxiechao.y20.common.core.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

import java.lang.reflect.Method;

public class RestServiceCache {

    private static final String CACHE_NAME = "y20-rest-service";

    private static final RestServiceCache instance = new RestServiceCache();
    public static RestServiceCache getInstance(){
        return instance;
    }

    private Redis redis;

    private RestServiceCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_NAME,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public String getMethodServiceName(Method method){

        String className = method.getDeclaringClass().getName();
        return getMethodServiceName(className);
    }

    public String getMethodServiceName(String className){
        String serviceName = (String)redis.execute(jedis -> {
            return jedis.hget(CACHE_NAME, className);
        });

        return serviceName;
    }

    public void setMethodServiceName(Class<?> cls, String serviceName){
        for(Class interfaceCls : cls.getInterfaces()){
            String api = interfaceCls.getName();
            if(api.endsWith("Rest")){
                redis.execute(jedis -> {
                    return jedis.hset(CACHE_NAME, api, serviceName);
                });
            }
        }
    }
}
