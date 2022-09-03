package com.airxiechao.y20.ip;

import com.airxiechao.y20.ip.rest.server.IpRestServer;

public class IpBoot {
    public static void main(String[] args){
        IpRestServer ipRestServer = IpRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ipRestServer.stop();
        }));

        ipRestServer.start();
    }
}
