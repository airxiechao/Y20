package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class PullFilePartExplorerRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String explorerRunInstanceUuid;
    @Required private String path;
    @Required private Long pos;
    @Required private Integer length;

    public PullFilePartExplorerRunInstanceParam() {
    }

    public PullFilePartExplorerRunInstanceParam(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, Long pos, Integer length) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
        this.pos = pos;
        this.length = length;
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

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
