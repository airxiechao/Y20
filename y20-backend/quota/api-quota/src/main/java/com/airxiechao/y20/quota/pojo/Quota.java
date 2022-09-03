package com.airxiechao.y20.quota.pojo;

import java.util.Date;

public class Quota {
    private int maxAgent;
    private int maxPipelineRun;
    private Date beginTime;
    private Date endTime;

    public Quota(int maxAgent, int maxPipelineRun, Date beginTime, Date endTime) {
        this.maxAgent = maxAgent;
        this.maxPipelineRun = maxPipelineRun;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public int getMaxAgent() {
        return maxAgent;
    }

    public void setMaxAgent(int maxAgent) {
        this.maxAgent = maxAgent;
    }

    public int getMaxPipelineRun() {
        return maxPipelineRun;
    }

    public void setMaxPipelineRun(int maxPipelineRun) {
        this.maxPipelineRun = maxPipelineRun;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
