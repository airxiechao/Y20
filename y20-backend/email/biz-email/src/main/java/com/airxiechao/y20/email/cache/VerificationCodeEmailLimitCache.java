package com.airxiechao.y20.email.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class VerificationCodeEmailLimitCache {

    private static final String CACHE_VERIFICATION_CODE_EMAIL_LIMIT = "y20-verification-code-email-limit";
    private static final int EXPIRE_SECS = 60;

    private static final VerificationCodeEmailLimitCache instance = new VerificationCodeEmailLimitCache();
    public static VerificationCodeEmailLimitCache getInstance(){
        return instance;
    }

    private Redis redis;

    private VerificationCodeEmailLimitCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_VERIFICATION_CODE_EMAIL_LIMIT,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public boolean exists(String email){
        String key = buildKey(email);

        return (boolean)redis.execute(jedis -> {
            return jedis.exists(key);
        });
    }

    public void put(String email){
        String key = buildKey(email);

        redis.execute(jedis -> {
            jedis.set(key, "1");
            jedis.expire(key, EXPIRE_SECS);
            return null;
        });
    }

    private String buildKey(String email){
        return CACHE_VERIFICATION_CODE_EMAIL_LIMIT + ":" + email;
    }

}
