package com.airxiechao.y20.common.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("common.yml")
public class CommonConfig {

    private RedisConfig redis;
    private RabbitmqConfig rabbitmq;

    private CommonConfig(){}

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    public RabbitmqConfig getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(RabbitmqConfig rabbitmq) {
        this.rabbitmq = rabbitmq;
    }
}
