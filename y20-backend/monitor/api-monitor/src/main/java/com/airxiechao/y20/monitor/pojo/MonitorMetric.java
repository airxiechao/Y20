package com.airxiechao.y20.monitor.pojo;

import com.airxiechao.y20.monitor.db.influxdb.record.MonitorMetricRecord;

public class MonitorMetric {
    private Long userId;
    private Long projectId;
    private Long monitorId;
    private Double cpuUsage;
    private Long memoryBytes;
    private Long timestamp;

    public MonitorMetricRecord toRecord() {
        MonitorMetricRecord record = new MonitorMetricRecord();
        record.setUserId(userId);
        record.setProjectId(projectId);
        record.setMonitorId(monitorId);
        record.setCpuUsage(cpuUsage);
        record.setMemoryBytes(memoryBytes);
        record.setTimestamp(timestamp);

        return record;
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
