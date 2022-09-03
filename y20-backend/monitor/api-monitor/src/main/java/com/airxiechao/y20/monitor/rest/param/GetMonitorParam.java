package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetMonitorParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long monitorId;

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
}
