package com.airxiechao.y20.monitor.pojo;

import com.airxiechao.axcboot.util.os.FileSystemMetric;
import com.airxiechao.y20.monitor.db.influxdb.record.AgentMetricRecord;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class AgentMetric {
    private Long userId;
    private String agentId;
    private Double cpuLoad;
    private Long memoryAvailableBytes;
    private Long memoryTotalBytes;
    private List<FileSystemMetric> filesystem;
    private Long timestamp;

    public AgentMetricRecord toRecord() {
        AgentMetricRecord record = new AgentMetricRecord();
        record.setUserId(userId);
        record.setAgentId(agentId);
        record.setCpuLoad(cpuLoad);
        record.setMemoryAvailableBytes(memoryAvailableBytes);
        record.setMemoryTotalBytes(memoryTotalBytes);
        record.setFilesystem(JSON.toJSONString(filesystem));
        record.setTimestamp(timestamp);

        return record;
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

    public List<FileSystemMetric> getFilesystem() {
        return filesystem;
    }

    public void setFilesystem(List<FileSystemMetric> filesystem) {
        this.filesystem = filesystem;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
