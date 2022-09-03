package com.airxiechao.y20.agent.rpc.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class RegisterAgentRpcParam {

    @Required private Long userId;
    @Required private String agentId;
    private String version;
    private String hostName;
    private String os;
    private String ip;

    public RegisterAgentRpcParam() {
    }

    public RegisterAgentRpcParam(Long userId, String agentId, String version, String hostName, String os, String ip) {
        this.userId = userId;
        this.agentId = agentId;
        this.version = version;
        this.hostName = hostName;
        this.os = os;
        this.ip = ip;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
