package com.airxiechao.y20.template;

import com.airxiechao.y20.template.rest.server.TemplateRestServer;

public class TemplateBoot {
    public static void main(String[] args){
        TemplateRestServer templateRestServer = TemplateRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            templateRestServer.stop();
        }));

        templateRestServer.start();
    }
}
