package com.airxiechao.y20.monitor.pojo;

import java.util.Map;

public class MonitorPipelineActionParam {
    private Long projectId;
    private Long pipelineId;
    private Map<String, String> inParams;

    public MonitorPipelineActionParam() {
    }

    public MonitorPipelineActionParam(Long projectId, Long pipelineId, Map<String, String> inParams) {
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.inParams = inParams;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }
}
