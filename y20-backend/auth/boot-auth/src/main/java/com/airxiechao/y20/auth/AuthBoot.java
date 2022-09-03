package com.airxiechao.y20.auth;

import com.airxiechao.y20.auth.rest.server.AuthRestServer;

public class AuthBoot {
    public static void main(String[] args){
        AuthRestServer authRestServer = AuthRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            authRestServer.stop();
        }));

        authRestServer.start();
    }
}
