package com.airxiechao.y20.artifact;

import com.airxiechao.y20.artifact.rest.ArtifactRestServer;

public class ArtifactBoot {

    public static void main(String[] args){
        ArtifactRestServer artifactRestServer = ArtifactRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            artifactRestServer.stop();
        }));

        artifactRestServer.start();
    }
}
