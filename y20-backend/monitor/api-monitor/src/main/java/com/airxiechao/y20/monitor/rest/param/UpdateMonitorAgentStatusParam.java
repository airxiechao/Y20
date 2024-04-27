package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.axcboot.util.os.FileSystemMetric;

import java.util.List;

public class UpdateMonitorAgentStatusParam {
    @Required private Long userId;
    @Required private String agentId;
    private Double cpuLoad;
    private Long memoryAvailableBytes;
    private Long memoryTotalBytes;
    private List<FileSystemMetric> filesystem;
    private Long timestamp;

    public UpdateMonitorAgentStatusParam() {
    }

    public UpdateMonitorAgentStatusParam(Long userId, String agentId, Double cpuLoad, Long memoryAvailableBytes, Long memoryTotalBytes, List<FileSystemMetric> filesystem, Long timestamp) {
        this.userId = userId;
        this.agentId = agentId;
        this.cpuLoad = cpuLoad;
        this.memoryAvailableBytes = memoryAvailableBytes;
        this.memoryTotalBytes = memoryTotalBytes;
        this.filesystem = filesystem;
        this.timestamp = timestamp;
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
