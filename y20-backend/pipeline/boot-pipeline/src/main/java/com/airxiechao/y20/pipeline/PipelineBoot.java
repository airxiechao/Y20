package com.airxiechao.y20.pipeline;

import com.airxiechao.y20.pipeline.rest.server.PipelineRestServer;

public class PipelineBoot {
    public static void main(String[] args){
        PipelineRestServer pipelineRestServer = PipelineRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pipelineRestServer.stop();
        }));

        pipelineRestServer.start();
    }
}
