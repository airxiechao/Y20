package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DownloadFileExplorerRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String explorerRunInstanceUuid;
    @Required private String path;

    public DownloadFileExplorerRunInstanceParam() {
    }

    public DownloadFileExplorerRunInstanceParam(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
