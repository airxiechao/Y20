package com.airxiechao.y20.pipeline;

import com.airxiechao.y20.pipeline.rest.server.PipelineRunInstanceRestServer;
import com.airxiechao.y20.pipeline.run.pipeline.PipelineRunInstanceFactory;

public class PipelineRunInstanceBoot {
    public static void main(String[] args){
        PipelineRunInstanceRestServer pipelineRunInstanceRestServer = PipelineRunInstanceRestServer.getInstance();
        PipelineRunInstanceFactory pipelineRunInstanceFactory = PipelineRunInstanceFactory.getInstance();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            pipelineRunInstanceRestServer.stop();
            pipelineRunInstanceFactory.shutdown();
        }));

        pipelineRunInstanceRestServer.start();
    }
}
