package com.airxiechao.y20.pay;

import com.airxiechao.y20.pay.rest.server.PayRestServer;

public class PayBoot {
    public static void main(String[] args){
        PayRestServer payRestServer = PayRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            payRestServer.stop();
        }));

        payRestServer.start();
    }
}
