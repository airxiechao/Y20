package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateAgentParam {

    @Required private Long userId;
    @Required private String agentId;
    private String clientId;
    private String version;
    private String ip;
    private String hostName;
    private String os;
    private String status;

    public UpdateAgentParam() {
    }

    public UpdateAgentParam(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os, String status) {
        this.userId = userId;
        this.agentId = agentId;
        this.clientId = clientId;
        this.version = version;
        this.ip = ip;
        this.hostName = hostName;
        this.os = os;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
