package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class PushFilePartExplorerRunInstanceParam {
    @Required private String pipelineRunInstanceUuid;
    @Required private String explorerRunInstanceUuid;
    @Required private String holderId;
    @Required private Long pos;
    @Required private byte[] bytes;

    public PushFilePartExplorerRunInstanceParam() {
    }

    public PushFilePartExplorerRunInstanceParam(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId, Long pos, byte[] bytes) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.holderId = holderId;
        this.pos = pos;
        this.bytes = bytes;
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

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
