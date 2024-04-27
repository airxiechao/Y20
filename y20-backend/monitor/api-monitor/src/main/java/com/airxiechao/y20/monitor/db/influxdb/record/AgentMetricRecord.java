package com.airxiechao.y20.monitor.db.influxdb.record;

import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbField;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbMeasurement;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbTag;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbTime;
import com.airxiechao.axcboot.util.os.FileSystemMetric;
import com.airxiechao.y20.monitor.pojo.AgentMetric;
import com.alibaba.fastjson.JSON;

@InfluxDbMeasurement("agent-metric")
public class AgentMetricRecord {
    @InfluxDbTag private Long userId;
    @InfluxDbTag private String agentId;
    @InfluxDbField private Double cpuLoad;
    @InfluxDbField private Long memoryAvailableBytes;
    @InfluxDbField private Long memoryTotalBytes;
    @InfluxDbField private String filesystem;
    @InfluxDbTime private Long timestamp;

    public AgentMetric toPojo() {
        AgentMetric metric = new AgentMetric();
        metric.setUserId(userId);
        metric.setAgentId(agentId);
        metric.setCpuLoad(cpuLoad);
        metric.setMemoryAvailableBytes(memoryAvailableBytes);
        metric.setMemoryTotalBytes(memoryTotalBytes);
        metric.setFilesystem(JSON.parseArray(filesystem, FileSystemMetric.class));
        metric.setTimestamp(timestamp);

        return metric;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(Double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public Long getMemoryAvailableBytes() {
        return memoryAvailableBytes;
    }

    public void setMemoryAvailableBytes(Long memoryAvailableBytes) {
        this.memoryAvailableBytes = memoryAvailableBytes;
    }

    public Long getMemoryTotalBytes() {
        return memoryTotalBytes;
    }

    public void setMemoryTotalBytes(Long memoryTotalBytes) {
        this.memoryTotalBytes = memoryTotalBytes;
    }

    public String getFilesystem() {
        return filesystem;
    }

    public void setFilesystem(String filesystem) {
        this.filesystem = filesystem;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
