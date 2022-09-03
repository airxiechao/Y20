package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DestroyTerminalRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String terminalRunInstanceUuid;

    public DestroyTerminalRunInstanceParam(String pipelineRunInstanceUuid, String terminalRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }
}
