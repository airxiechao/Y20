package com.airxiechao.y20.monitor;

import com.airxiechao.y20.monitor.rest.server.MonitorRestServer;

public class MonitorBoot {
    public static void main(String[] args){
        MonitorRestServer projectRestServer = MonitorRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            projectRestServer.stop();
        }));

        projectRestServer.start();
    }
}
