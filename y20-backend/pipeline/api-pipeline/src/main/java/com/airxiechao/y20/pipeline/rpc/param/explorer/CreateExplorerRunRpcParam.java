package com.airxiechao.y20.pipeline.rpc.param.explorer;

public class CreateExplorerRunRpcParam {

    private String pipelineRunInstanceUuid;
    private String dockerContainerId;
    private String workingDir;

    public CreateExplorerRunRpcParam() {
    }

    public CreateExplorerRunRpcParam(String pipelineRunInstanceUuid, String dockerContainerId, String workingDir) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.dockerContainerId = dockerContainerId;
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

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
