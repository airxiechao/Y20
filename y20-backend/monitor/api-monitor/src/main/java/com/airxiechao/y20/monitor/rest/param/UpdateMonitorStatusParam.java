package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateMonitorStatusParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long monitorId;
    @Required private String status;
    private Double cpuUsage;
    private Long memoryBytes;
    private Long timestamp;

    public UpdateMonitorStatusParam() {
    }

    public UpdateMonitorStatusParam(Long userId, Long projectId, Long monitorId, String status,
                                    Double cpuUsage, Long memoryBytes, Long timestamp) {
        this.userId = userId;
        this.projectId = projectId;
        this.monitorId = monitorId;
        this.status = status;
        this.cpuUsage = cpuUsage;
        this.memoryBytes = memoryBytes;
        this.timestamp = timestamp;
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

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getMemoryBytes() {
        return memoryBytes;
    }

    public void setMemoryBytes(Long memoryBytes) {
        this.memoryBytes = memoryBytes;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
