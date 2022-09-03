package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-server.yml")
public class AgentServerConfig {

    private String name;
    private int port;
    private int rpcPort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

}
