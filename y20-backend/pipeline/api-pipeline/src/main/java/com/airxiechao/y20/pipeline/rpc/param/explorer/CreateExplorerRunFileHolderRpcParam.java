package com.airxiechao.y20.pipeline.rpc.param.explorer;

public class CreateExplorerRunFileHolderRpcParam {

    private String explorerRunInstanceUuid;
    private String path;
    private Long size;

    public CreateExplorerRunFileHolderRpcParam() {
    }

    public CreateExplorerRunFileHolderRpcParam(String explorerRunInstanceUuid, String path, Long size) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
        this.size = size;
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

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
