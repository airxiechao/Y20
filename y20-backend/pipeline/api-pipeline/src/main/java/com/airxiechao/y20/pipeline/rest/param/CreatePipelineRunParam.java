package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

import java.util.Map;

public class CreatePipelineRunParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private String name;
    private Map<String, String> inParams;
    private Boolean flagDebug;

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

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getInParams() {
        return inParams;
    }

    public void setInParams(Map<String, String> inParams) {
        this.inParams = inParams;
    }

    public Boolean getFlagDebug() {
        return flagDebug;
    }

    public void setFlagDebug(Boolean flagDebug) {
        this.flagDebug = flagDebug;
    }
}
