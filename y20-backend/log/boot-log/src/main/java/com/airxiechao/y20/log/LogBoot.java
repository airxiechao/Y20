package com.airxiechao.y20.log;

import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.log.rest.server.LogRestServer;

public class LogBoot {
    public static void main(String[] args){
        LogRestServer logRestServer = LogRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logRestServer.stop();
        }));

        EventBus.getInstance().registerSinks(Meta.getModulePackageName(LogBoot.class));
        logRestServer.start();
    }
}
