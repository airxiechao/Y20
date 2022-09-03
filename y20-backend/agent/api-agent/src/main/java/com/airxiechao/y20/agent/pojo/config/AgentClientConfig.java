package com.airxiechao.y20.agent.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("agent-client.yml")
public class AgentClientConfig {

    private String serverHost;
    private String agentId;
    private String accessToken;
    private int serverRpcPort;
    private boolean serverRestUseSsl;
    private String dataDir;
    private String clientJks;
    private String clientJksPassword;
    private String trustJks;
    private String trustJksPassword;

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

    public int getServerRpcPort() {
        return serverRpcPort;
    }

    public void setServerRpcPort(int serverRpcPort) {
        this.serverRpcPort = serverRpcPort;
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

    public String getClientJks() {
        return clientJks;
    }

    public void setClientJks(String clientJks) {
        this.clientJks = clientJks;
    }

    public String getClientJksPassword() {
        return clientJksPassword;
    }

    public void setClientJksPassword(String clientJksPassword) {
        this.clientJksPassword = clientJksPassword;
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

