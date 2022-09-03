package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

import java.util.Map;

public class ServiceCreatePipelineRunParam {
    @Required private Long pipelineId;
    @Required private String name;
    private Map<String, String> inParams;
    private Boolean flagDebug;

    public ServiceCreatePipelineRunParam() {
    }

    public ServiceCreatePipelineRunParam(Long pipelineId, String name, Map<String, String> inParams, Boolean flagDebug) {
        this.pipelineId = pipelineId;
        this.name = name;
        this.inParams = inParams;
        this.flagDebug = flagDebug;
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
