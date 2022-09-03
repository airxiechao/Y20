package com.airxiechao.y20.pipeline.rpc.param.explorer;

public class RemoveExplorerRunFileHolderRpcParam {
    private String explorerRunInstanceUuid;
    private String fileHolderId;

    public RemoveExplorerRunFileHolderRpcParam() {
    }

    public RemoveExplorerRunFileHolderRpcParam(String explorerRunInstanceUuid, String fileHolderId) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.fileHolderId = fileHolderId;
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
}
