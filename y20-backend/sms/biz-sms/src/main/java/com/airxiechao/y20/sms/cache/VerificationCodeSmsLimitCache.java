package com.airxiechao.y20.sms.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class VerificationCodeSmsLimitCache {

    private static final String CACHE_VERIFICATION_CODE_SMS_LIMIT = "y20-verification-code-sms-limit";
    private static final int EXPIRE_SECS = 60;

    private static final VerificationCodeSmsLimitCache instance = new VerificationCodeSmsLimitCache();
    public static VerificationCodeSmsLimitCache getInstance(){
        return instance;
    }

    private Redis redis;

    private VerificationCodeSmsLimitCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_VERIFICATION_CODE_SMS_LIMIT,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public boolean exists(String mobile){
        String key = buildKey(mobile);

        return (boolean)redis.execute(jedis -> {
            return jedis.exists(key);
        });
    }

    public void put(String mobile){
        String key = buildKey(mobile);

        redis.execute(jedis -> {
            jedis.set(key, "1");
            jedis.expire(key, EXPIRE_SECS);
            return null;
        });
    }

    private String buildKey(String mobile){
        return CACHE_VERIFICATION_CODE_SMS_LIMIT + ":" + mobile;
    }

}
