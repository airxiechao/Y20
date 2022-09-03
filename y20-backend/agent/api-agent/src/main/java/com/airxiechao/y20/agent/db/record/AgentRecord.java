package com.airxiechao.y20.agent.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.agent.pojo.Agent;

import java.util.Date;

@Table("agent")
@Index(fields = {"userId", "agentId"}, unique = true)
@Index(fields = {"clientId"}, unique = true)
public class AgentRecord {

    private Long id;
    private Long userId;
    @Column(length = 100) private String agentId;
    @Column(length = 100) private String agentSecret;
    @Column(length = 50) private String clientId;
    @Column(length = 50) private String version;
    private String ip;
    private String hostName;
    private String os;
    private String status;
    private Boolean flagUpgrading;
    private Boolean flagRestarting;
    private Date lastHeartbeatTime;

    public Agent toPojo(){
        return new Agent(userId, clientId, agentId, version, ip, hostName, os, status, flagUpgrading, flagRestarting, lastHeartbeatTime);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAgentSecret() {
        return agentSecret;
    }

    public void setAgentSecret(String agentSecret) {
        this.agentSecret = agentSecret;
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
