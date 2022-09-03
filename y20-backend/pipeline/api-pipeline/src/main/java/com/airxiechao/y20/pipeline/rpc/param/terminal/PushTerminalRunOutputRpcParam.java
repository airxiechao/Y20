package com.airxiechao.y20.pipeline.rpc.param.terminal;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class PushTerminalRunOutputRpcParam {

    private String pipelineRunInstanceUuid;
    private String terminalRunInstanceUuid;
    private String output;

    public PushTerminalRunOutputRpcParam() {
    }

    public PushTerminalRunOutputRpcParam(String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String output) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
        this.output = output;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }

    public void setTerminalRunInstanceUuid(String terminalRunInstanceUuid) {
        this.terminalRunInstanceUuid = terminalRunInstanceUuid;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
