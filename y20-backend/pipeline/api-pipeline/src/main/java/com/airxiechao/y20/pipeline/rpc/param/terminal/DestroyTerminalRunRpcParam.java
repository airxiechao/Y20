package com.airxiechao.y20.pipeline.rpc.param.terminal;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class DestroyTerminalRunRpcParam {
    
    private String terminalRunInstanceUuid;

    public DestroyTerminalRunRpcParam() {
    }

    public DestroyTerminalRunRpcParam(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }
}
