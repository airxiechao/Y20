package com.airxiechao.y20.auth.cache;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.cache.redis.Redis;
import com.airxiechao.axcboot.storage.cache.redis.RedisManager;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

public class LoginByUsernameFailLimitCache {

    private static final String CACHE_LOGIN_BY_USERNAME_FAIL_LIMIT = "y20-login-by-username-fail-limit";
    private static final int EXPIRE_SECS = 60;
    private static final int MAX_NUM_FAIL = 10;

    private static final LoginByUsernameFailLimitCache instance = new LoginByUsernameFailLimitCache();
    public static LoginByUsernameFailLimitCache getInstance(){
        return instance;
    }

    private Redis redis;

    private LoginByUsernameFailLimitCache(){
        redis = RedisManager.getInstance().createRedis(CACHE_LOGIN_BY_USERNAME_FAIL_LIMIT,
                ConfigFactory.get(CommonConfig.class).getRedis().getHost(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPort(),
                ConfigFactory.get(CommonConfig.class).getRedis().getPassword(),
                ConfigFactory.get(CommonConfig.class).getRedis().getMaxPoolSize());
    }

    public boolean exceeds(String username){
        String key = buildKey(username);

        return (boolean)redis.execute(jedis -> {
            String fail = jedis.get(key);
            if(null == fail){
                return false;
            }

            int numFail = Integer.valueOf(fail);
            return numFail >= MAX_NUM_FAIL;
        });
    }

    public void add(String username){
        String key = buildKey(username);

        redis.execute(jedis -> {
            jedis.incr(key);
            jedis.expire(key, EXPIRE_SECS);
            return null;
        });
    }

    private String buildKey(String username){
        return CACHE_LOGIN_BY_USERNAME_FAIL_LIMIT + ":" + username;
    }
}
