package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-server.yml")
public class AgentServerConfig {

    private String name;
    private int port;
    private int rpcPort;
    private String serverJks;
    private String serverJksPassword;
    private String trustJks;
    private String trustJksPassword;

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

    public String getServerJks() {
        return serverJks;
    }

    public void setServerJks(String serverJks) {
        this.serverJks = serverJks;
    }

    public String getServerJksPassword() {
        return serverJksPassword;
    }

    public void setServerJksPassword(String serverJksPassword) {
        this.serverJksPassword = serverJksPassword;
    }

    public String getTrustJks() {
        return trustJks;
    }

    public void setTrustJks(String trustJks) {
        this.trustJks = trustJks;
    }

    public String getTrustJksPassword() {
        return trustJksPassword;
    }

    public void setTrustJksPassword(String trustJksPassword) {
        this.trustJksPassword = trustJksPassword;
    }
}
