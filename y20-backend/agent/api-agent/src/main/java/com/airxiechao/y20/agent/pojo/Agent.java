package com.airxiechao.y20.agent.pojo;

import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.agent.db.record.AgentRecord;

import java.util.Date;

public class Agent {

    private Long userId;
    private String clientId;
    private String agentId;
    private String version;
    private String ip;
    private String hostName;
    private String os;
    private String status;
    private Boolean flagUpgrading;
    private Boolean flagRestarting;
    private Date lastHeartbeatTime;

    public Agent(
            Long userId, String clientId, String agentId, String version, String ip, String hostName, String os,
            String status, Boolean flagUpgrading, Boolean flagRestarting, Date lastHeartbeatTime
    ) {
        this.userId = userId;
        this.agentId = agentId;
        this.clientId = clientId;
        this.version = version;
        this.ip = ip;
        this.hostName = hostName;
        this.os = os;
        this.status = status;
        this.flagUpgrading = flagUpgrading;
        this.flagRestarting = flagRestarting;
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public AgentRecord toRecord() {
        AgentRecord agentRecord = new AgentRecord();
        ModelUtil.copyProperties(this, agentRecord);

        return agentRecord;
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
