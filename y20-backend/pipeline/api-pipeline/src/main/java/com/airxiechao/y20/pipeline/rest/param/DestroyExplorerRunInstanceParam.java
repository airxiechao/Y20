package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DestroyExplorerRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String explorerRunInstanceUuid;

    public DestroyExplorerRunInstanceParam() {
    }

    public DestroyExplorerRunInstanceParam(String pipelineRunInstanceUuid, String explorerRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }
}
