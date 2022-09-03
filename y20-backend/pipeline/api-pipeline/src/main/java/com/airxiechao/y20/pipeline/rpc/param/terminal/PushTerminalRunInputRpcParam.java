package com.airxiechao.y20.pipeline.rpc.param.terminal;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class PushTerminalRunInputRpcParam {

    private String terminalRunInstanceUuid;
    private String input;

    public PushTerminalRunInputRpcParam() {
    }

    public PushTerminalRunInputRpcParam(String terminalRunInstanceUuid, String input) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.input = input;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
