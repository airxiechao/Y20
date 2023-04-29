package com.airxiechao.y20.scriptlib;

import com.airxiechao.y20.scriptlib.rest.server.ScriptLibRestServer;

public class ScriptLibBoot {
    public static void main(String[] args){
        ScriptLibRestServer scriptLibRestServer = ScriptLibRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scriptLibRestServer.stop();
        }));

        scriptLibRestServer.start();
    }
}
