package com.airxiechao.y20.template;

import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.template.rest.server.TemplateRestServer;
import com.airxiechao.y20.template.search.TemplateIndex;

public class TemplateBoot {
    public static void main(String[] args){
        TemplateIndex templateIndex = TemplateIndex.getInstance();

        TemplateRestServer templateRestServer = TemplateRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            templateRestServer.stop();
            templateIndex.close();
        }));

        EventBus.getInstance().registerSinks(Meta.getModulePackageName(TemplateBoot.class));

        templateIndex.init();
        templateRestServer.start();
    }
}
