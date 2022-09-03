package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class StartPipelineRunInstanceParam {

    @Required private String pipelineRunInstanceUuid;
    @Required private Boolean flagDebug;

    public StartPipelineRunInstanceParam() {
    }

    public StartPipelineRunInstanceParam(String pipelineRunInstanceUuid, Boolean flagDebug) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.flagDebug = flagDebug;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public Boolean getFlagDebug() {
        return flagDebug;
    }

    public void setFlagDebug(Boolean flagDebug) {
        this.flagDebug = flagDebug;
    }
}
