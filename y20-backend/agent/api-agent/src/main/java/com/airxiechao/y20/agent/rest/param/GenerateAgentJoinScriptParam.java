package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GenerateAgentJoinScriptParam {
    @Required private Long userId;
    @Required private String osType;
    @Required private String agentId;
    @Required private String accessToken;
    @Required private String serverHost;
    @Required private Integer serverRpcPort;
    @Required private Boolean serverRestUseSsl;
    @Required private String dataDir;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
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

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerRpcPort() {
        return serverRpcPort;
    }

    public void setServerRpcPort(Integer serverRpcPort) {
        this.serverRpcPort = serverRpcPort;
    }

    public Boolean getServerRestUseSsl() {
        return serverRestUseSsl;
    }

    public void setServerRestUseSsl(Boolean serverRestUseSsl) {
        this.serverRestUseSsl = serverRestUseSsl;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
}
