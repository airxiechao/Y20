package com.airxiechao.y20.monitor.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.alibaba.fastjson.JSON;

import java.util.Date;

@Table("monitor")
@Index(fields = {"userId", "projectId"})
@Index(fields = {"agentId"})
public class MonitorRecord {
    private Long id;
    private Long userId;
    private Long projectId;
    @Column(length = 100) private String name;
    @Column(length = 100) private String agentId;
    @Column(length = 50) private String type;
    @Column(type = "text") private String target;
    @Column(length = 50) private String actionType;
    @Column(type = "text") private String actionParam;
    private String status;
    private Date createTime;
    private Date lastUpdateTime;

    public Monitor toPojo(){
        Monitor monitor = new Monitor();
        monitor.setMonitorId(id);
        monitor.setUserId(userId);
        monitor.setProjectId(projectId);
        monitor.setName(name);
        monitor.setAgentId(agentId);
        monitor.setType(type);
        monitor.setTarget(JSON.parseObject(target));
        monitor.setActionType(actionType);
        monitor.setActionParam(JSON.parseObject(actionParam));
        monitor.setStatus(status);
        monitor.setCreateTime(createTime);
        monitor.setLastUpdateTime(lastUpdateTime);

        return monitor;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
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

