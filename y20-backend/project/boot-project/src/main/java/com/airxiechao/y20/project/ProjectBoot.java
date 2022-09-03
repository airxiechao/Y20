package com.airxiechao.y20.project;

import com.airxiechao.y20.project.rest.server.ProjectRestServer;

public class ProjectBoot {

    public static void main(String[] args){
        ProjectRestServer projectRestServer = ProjectRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            projectRestServer.stop();
        }));

        projectRestServer.start();
    }
}
