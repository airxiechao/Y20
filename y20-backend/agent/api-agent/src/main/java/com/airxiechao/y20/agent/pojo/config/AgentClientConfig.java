package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-client.yml")
public class AgentClientConfig {

    private String serverHost;
    private String agentId;
    private String accessToken;
    private Integer serverRpcPort;
    private Integer serverRestPort;
    private boolean serverRestUseSsl;
    private String dataDir;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getServerRpcPort() {
        return serverRpcPort;
    }

    public void setServerRpcPort(Integer serverRpcPort) {
        this.serverRpcPort = serverRpcPort;
    }

    public Integer getServerRestPort() {
        return serverRestPort;
    }

    public void setServerRestPort(Integer serverRestPort) {
        this.serverRestPort = serverRestPort;
    }

    public boolean isServerRestUseSsl() {
        return serverRestUseSsl;
    }

    public void setServerRestUseSsl(boolean serverRestUseSsl) {
        this.serverRestUseSsl = serverRestUseSsl;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

}

