package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class InputTerminalRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String terminalRunInstanceUuid;
    @Required private String message;

    public InputTerminalRunInstanceParam(String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String message) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
