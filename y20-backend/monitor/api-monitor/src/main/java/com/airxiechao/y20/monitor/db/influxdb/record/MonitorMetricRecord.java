package com.airxiechao.y20.monitor.db.influxdb.record;

import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbField;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbMeasurement;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbTag;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbTime;
import com.airxiechao.y20.monitor.pojo.MonitorMetric;

@InfluxDbMeasurement("monitor-metric")
public class MonitorMetricRecord {
    @InfluxDbTag private Long userId;
    @InfluxDbTag private Long projectId;
    @InfluxDbTag private Long monitorId;
    @InfluxDbField private Double cpuUsage;
    @InfluxDbField private Long memoryBytes;
    @InfluxDbTime private Long timestamp;

    public MonitorMetric toPojo() {
        MonitorMetric metric = new MonitorMetric();
        metric.setUserId(userId);
        metric.setProjectId(projectId);
        metric.setMonitorId(monitorId);
        metric.setCpuUsage(cpuUsage);
        metric.setMemoryBytes(memoryBytes);
        metric.setTimestamp(timestamp);

        return metric;
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
