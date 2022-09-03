package com.airxiechao.y20.pipeline.rpc.param.explorer;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class ListExplorerRunRpcParam {

    private String explorerRunInstanceUuid;
    private String path;

    public ListExplorerRunRpcParam() {
    }

    public ListExplorerRunRpcParam(String explorerRunInstanceUuid, String path) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
        this.path = path;
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
