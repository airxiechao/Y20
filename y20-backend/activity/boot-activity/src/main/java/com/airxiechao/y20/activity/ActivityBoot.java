package com.airxiechao.y20.activity;

import com.airxiechao.y20.activity.rest.server.ActivityRestServer;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;

public class ActivityBoot {
    public static void main(String[] args){

        // rest server
        ActivityRestServer activityRestServer = ActivityRestServer.getInstance();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activityRestServer.stop();
        }));

        // register event sinks
        EventBus.getInstance().registerSinks(Meta.getModulePackageName(ActivityBoot.class));

        activityRestServer.start();
    }
}
