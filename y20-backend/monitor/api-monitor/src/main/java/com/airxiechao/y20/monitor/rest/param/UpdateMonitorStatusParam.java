package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateMonitorStatusParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long monitorId;
    @Required private String status;

    public UpdateMonitorStatusParam() {
    }

    public UpdateMonitorStatusParam(Long userId, Long projectId, Long monitorId, String status) {
        this.userId = userId;
        this.projectId = projectId;
        this.monitorId = monitorId;
        this.status = status;
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

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
