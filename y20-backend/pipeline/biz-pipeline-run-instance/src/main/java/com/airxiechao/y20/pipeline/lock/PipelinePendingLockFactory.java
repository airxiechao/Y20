package com.airxiechao.y20.pipeline.lock;


import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisLock;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class PipelinePendingLockFactory {

    private static final String CACHE_PIPELINE_PENDING = "y20-pipeline-pending";
    private static final String LOCK_KEY_PREFIX = "y20-pipeline-pending-lock";

    private static final PipelinePendingLockFactory instance = new PipelinePendingLockFactory();
    public static PipelinePendingLockFactory getInstance() {
        return instance;
    }

    private CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);
    private Redis redis;

    private PipelinePendingLockFactory(){
        this.redis = RedisManager.getInstance().createRedis(
                CACHE_PIPELINE_PENDING,
                commonConfig.getRedis().getHost(),
                commonConfig.getRedis().getPort(),
                commonConfig.getRedis().getPassword(),
                commonConfig.getRedis().getMaxPoolSize()
        );
    }

    public RedisLock get(Long pipelineId, int numExpireSec){
        RedisLock redisLock = new RedisLock(redis, buildKey(pipelineId), numExpireSec);
        return redisLock;
    }

    private String buildKey(Long pipelineId){
        return String.format("%s:%d", LOCK_KEY_PREFIX, pipelineId);
    }
}