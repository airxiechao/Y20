package com.airxiechao.y20.agent.caller;

import com.airxiechao.y20.agent.rpc.reverse.AgentRpcCaller;

public class AgentCallerBoot {

    public static void main(String[] args) {
        AgentRpcCaller caller = new AgentRpcCaller();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            caller.stop();
        }));

        caller.connectAgent("127.0.0.1");
    }

}
