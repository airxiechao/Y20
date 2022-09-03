package com.airxiechao.y20.email;

import com.airxiechao.y20.email.rest.server.EmailRestServer;

public class EmailBoot {

    public static void main(String[] args){
        EmailRestServer emailRestServer = EmailRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            emailRestServer.stop();
        }));

        emailRestServer.start();
    }
}
