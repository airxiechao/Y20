package com.airxiechao.y20.pipeline.rpc.param.explorer;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class PushExplorerRunFilePartRpcParam {

    private String explorerRunInstanceUuid;
    private String fileHolderId;
    private Long pos;
    private byte[] bytes;

    public PushExplorerRunFilePartRpcParam() {
    }

    public PushExplorerRunFilePartRpcParam(String explorerRunInstanceUuid, String fileHolderId, Long pos, byte[] bytes) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.fileHolderId = fileHolderId;
        this.pos = pos;
        this.bytes = bytes;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getFileHolderId() {
        return fileHolderId;
    }

    public void setFileHolderId(String fileHolderId) {
        this.fileHolderId = fileHolderId;
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
