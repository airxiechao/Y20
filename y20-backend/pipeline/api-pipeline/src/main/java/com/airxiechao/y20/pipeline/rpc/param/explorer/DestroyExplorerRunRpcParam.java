package com.airxiechao.y20.pipeline.rpc.param.explorer;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class DestroyExplorerRunRpcParam {

    private String explorerRunInstanceUuid;

    public DestroyExplorerRunRpcParam() {
    }

    public DestroyExplorerRunRpcParam(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }

    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }

    public void setExplorerRunInstanceUuid(String explorerRunInstanceUuid) {
        this.explorerRunInstanceUuid = explorerRunInstanceUuid;
    }
}
