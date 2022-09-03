package com.airxiechao.y20.cron.lock;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisLock;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class PipelineCronTaskLockFactory {

    private static final String CACHE_CRON = "y20-cron";
    private static final String LOCK_KEY_PREFIX = "y20-pipeline-cron-task-lock";

    private static final PipelineCronTaskLockFactory instance = new PipelineCronTaskLockFactory();
    public static PipelineCronTaskLockFactory getInstance() {
        return instance;
    }

    private CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);
    private Redis redis;

    private PipelineCronTaskLockFactory(){
        this.redis = RedisManager.getInstance().createRedis(
                CACHE_CRON,
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
