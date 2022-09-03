package com.airxiechao.y20.common.core.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class AgentServiceTagCache {

    private static final String CACHE_NAME = "y20-agent-service-tag";

    private static final AgentServiceTagCache instance = new AgentServiceTagCache();
    public static AgentServiceTagCache getInstance(){
        return instance;
    }

    private Redis redis;

    private AgentServiceTagCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_NAME,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public String getAgentServiceTag(String clientId){
        String serviceName = (String)redis.execute(jedis -> {
            return jedis.hget(CACHE_NAME, clientId);
        });

        return serviceName;
    }

    public void removeAgentServiceTag(String clientId){
        redis.execute(jedis -> {
            return jedis.hdel(CACHE_NAME, clientId);
        });
    }

    public void setAgentServiceTag(String clientId, String serviceTag){
        redis.execute(jedis -> {
            return jedis.hset(CACHE_NAME, clientId, serviceTag);
        });
    }
}
