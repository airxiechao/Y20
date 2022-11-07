package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CreateMonitorParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private String name;
    @Required private String agentId;
    @Required private String type;
    @Required private Object target;
    @Required private String actionType;
    private Object actionParam;

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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Object getActionParam() {
        return actionParam;
    }

    public void setActionParam(Object actionParam) {
        this.actionParam = actionParam;
    }
}
