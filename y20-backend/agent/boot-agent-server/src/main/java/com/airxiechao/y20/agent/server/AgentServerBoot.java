package com.airxiechao.y20.agent.server;

import com.airxiechao.y20.agent.rest.server.AgentServerRestServer;
import com.airxiechao.y20.agent.rpc.server.AgentRpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentServerBoot {

    private static final Logger logger = LoggerFactory.getLogger(AgentServerBoot.class);

    public static void main(String[] args) {
        // rest server
        AgentServerRestServer agentRestServer = AgentServerRestServer.getInstance();

        // rpc server
        AgentRpcServer agentRpcServer = AgentRpcServer.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            agentRestServer.stop();
            agentRpcServer.stop();
        }));

        agentRestServer.start();
        agentRpcServer.start();

    }
}
