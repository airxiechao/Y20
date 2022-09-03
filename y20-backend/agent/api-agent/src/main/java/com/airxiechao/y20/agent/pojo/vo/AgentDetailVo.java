package com.airxiechao.y20.agent.pojo.vo;

import com.airxiechao.y20.agent.pojo.Agent;

import java.util.Date;

public class AgentDetailVo extends AgentVo{

    protected Boolean running;
    protected String accessTokenName;
    protected Date accessTokenEndTime;

    public AgentDetailVo(Agent agent, boolean active, boolean running, String accessTokenName, Date accessTokenEndTime) {
        super(agent, active);
        this.running = running;
        this.accessTokenName = accessTokenName;
        this.accessTokenEndTime = accessTokenEndTime;
    }

    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public String getAccessTokenName() {
        return accessTokenName;
    }

    public void setAccessTokenName(String accessTokenName) {
        this.accessTokenName = accessTokenName;
    }

    public Date getAccessTokenEndTime() {
        return accessTokenEndTime;
    }

    public void setAccessTokenEndTime(Date accessTokenEndTime) {
        this.accessTokenEndTime = accessTokenEndTime;
    }
}
