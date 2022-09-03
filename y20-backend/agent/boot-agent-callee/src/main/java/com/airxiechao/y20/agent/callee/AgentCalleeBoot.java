package com.airxiechao.y20.agent.callee;

import com.airxiechao.y20.agent.rpc.reverse.AgentRpcCallee;

public class AgentCalleeBoot {
    public static void main(String[] args) throws Exception {
        AgentRpcCallee callee = new AgentRpcCallee("aaa");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            callee.stop();
        }));

        callee.syncStart();

        Thread.currentThread().join();
    }
}
