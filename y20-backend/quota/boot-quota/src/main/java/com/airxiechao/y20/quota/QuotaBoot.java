package com.airxiechao.y20.quota;

import com.airxiechao.y20.quota.rest.server.QuotaRestServer;

public class QuotaBoot {
    public static void main(String[] args){
        QuotaRestServer quotaRestServer = QuotaRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            quotaRestServer.stop();
        }));

        quotaRestServer.start();
    }
}
