package com.airxiechao.y20.manmachinetest;

import com.airxiechao.y20.manmachinetest.rest.server.ManMachineTestRestServer;

public class ManMachineTestBoot {

    public static void main(String[] args){
        ManMachineTestRestServer manMachineTestRestServer = ManMachineTestRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            manMachineTestRestServer.stop();
        }));

        manMachineTestRestServer.start();
    }
}
