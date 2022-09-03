package com.airxiechao.y20.common.core.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class PipelineRunInstanceServiceTagCache {

    private static final String CACHE_NAME = "y20-pipeline-run-instance-service-tag";

    private static final PipelineRunInstanceServiceTagCache instance = new PipelineRunInstanceServiceTagCache();
    public static PipelineRunInstanceServiceTagCache getInstance(){
        return instance;
    }

    private Redis redis;

    private PipelineRunInstanceServiceTagCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_NAME,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public String getPipelineRunInstanceServiceTag(String pipelineRunInstanceUuid){
        String serviceName = (String)redis.execute(jedis -> {
            return jedis.hget(CACHE_NAME, pipelineRunInstanceUuid);
        });

        return serviceName;
    }

    public void removePipelineRunInstanceServiceTag(String pipelineRunInstanceUuid){
        redis.execute(jedis -> {
            return jedis.hdel(CACHE_NAME, pipelineRunInstanceUuid);
        });
    }

    public void setPipelineRunInstanceServiceTag(String pipelineRunInstanceUuid, String serviceTag){
        redis.execute(jedis -> {
            return jedis.hset(CACHE_NAME, pipelineRunInstanceUuid, serviceTag);
        });
    }
}
