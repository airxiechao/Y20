package com.airxiechao.y20.agent.pojo.vo;

import com.airxiechao.y20.agent.pojo.Agent;

import java.util.Date;

public class AgentVo {
    protected Long userId;
    protected String agentId;
    protected String clientId;
    protected String version;
    protected String ip;
    protected String hostName;
    protected String os;
    protected Boolean active;
    protected Boolean flagUpgrading;
    protected Boolean flagRestarting;
    protected Date lastHeartbeatTime;

    public AgentVo() {
    }

    public AgentVo(Agent agent, boolean active) {
        this.userId = agent.getUserId();
        this.agentId = agent.getAgentId();
        this.clientId = agent.getClientId();
        this.version = agent.getVersion();
        this.ip = agent.getIp();
        this.hostName = agent.getHostName();
        this.os = agent.getOs();
        this.active = active;
        this.flagUpgrading = agent.getFlagUpgrading();
        this.flagRestarting = agent.getFlagRestarting();
        this.lastHeartbeatTime = agent.getLastHeartbeatTime();
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getFlagUpgrading() {
        return flagUpgrading;
    }

    public void setFlagUpgrading(Boolean flagUpgrading) {
        this.flagUpgrading = flagUpgrading;
    }

    public Boolean getFlagRestarting() {
        return flagRestarting;
    }

    public void setFlagRestarting(Boolean flagRestarting) {
        this.flagRestarting = flagRestarting;
    }

    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

}
