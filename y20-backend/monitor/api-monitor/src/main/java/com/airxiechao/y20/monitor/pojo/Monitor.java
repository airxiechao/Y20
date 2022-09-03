package com.airxiechao.y20.monitor.pojo;

import com.airxiechao.y20.monitor.db.record.MonitorRecord;
import com.alibaba.fastjson.JSON;

import java.util.Date;

public class Monitor  {
    private Long monitorId;
    private Long userId;
    private Long projectId;
    private String name;
    private String agentId;
    private String type;
    private Object target;
    private String status;
    private Date createTime;
    private Date lastUpdateTime;

    public MonitorRecord toRecord(){
        MonitorRecord record = new MonitorRecord();
        record.setId(monitorId);
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setName(name);
        record.setAgentId(agentId);
        record.setType(type);
        record.setTarget(JSON.toJSONString(target));
        record.setStatus(status);
        record.setCreateTime(createTime);
        record.setLastUpdateTime(lastUpdateTime);

        return record;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
