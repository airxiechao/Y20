package com.airxiechao.y20.agent;

import com.airxiechao.y20.agent.rest.server.AgentRestServer;

public class AgentBoot {

    public static void main(String[] args){
        AgentRestServer agentRestServer = AgentRestServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            agentRestServer.stop();
        }));

        agentRestServer.start();
    }
}
