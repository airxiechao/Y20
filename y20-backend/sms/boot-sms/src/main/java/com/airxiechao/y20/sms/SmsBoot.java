package com.airxiechao.y20.sms;

import com.airxiechao.y20.sms.rest.server.SmsRestServer;

public class SmsBoot {

    public static void main(String[] args){
        SmsRestServer smsRestServer = SmsRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            smsRestServer.stop();
        }));

        smsRestServer.start();
    }
}
