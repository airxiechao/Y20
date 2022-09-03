package com.airxiechao.y20.pipeline.rpc.param.terminal;

import com.airxiechao.axcboot.util.MapBuilder;

import java.util.Map;

public class CreateTerminalRunRpcParam {

    private String pipelineRunInstanceUuid;
    private String dockerContainerId;
    private String terminalType;
    private String workingDir;

    public CreateTerminalRunRpcParam() {
    }

    public CreateTerminalRunRpcParam(String pipelineRunInstanceUuid, String dockerContainerId, String terminalType, String workingDir) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.dockerContainerId = dockerContainerId;
        this.terminalType = terminalType;
        this.workingDir = workingDir;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getDockerContainerId() {
        return dockerContainerId;
    }

    public void setDockerContainerId(String dockerContainerId) {
        this.dockerContainerId = dockerContainerId;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
