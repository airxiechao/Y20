package com.airxiechao.y20.common.pipeline.env;

public class Env {
    private Long userId;
    private Long projectId;
    private Long pipelineId;
    private Long pipelineRunId;
    private String pipelineRunInstanceUuid;
    private String agentId;
    private String dockerImage;
    private String dockerHostWorkingDir;
    private String workingDir;
    private String dockerContainerId;
    private String envInstanceUuid;

    public Env() {
    }

    public Env(Long userId, Long projectId, Long pipelineId, Long pipelineRunId, String pipelineRunInstanceUuid, String agentId, String dockerImage, String dockerHostWorkingDir, String workingDir, String dockerContainerId, String envInstanceUuid) {
        this.userId = userId;
        this.projectId = projectId;
        this.pipelineId = pipelineId;
        this.pipelineRunId = pipelineRunId;
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.agentId = agentId;
        this.dockerImage = dockerImage;
        this.dockerHostWorkingDir = dockerHostWorkingDir;
        this.workingDir = workingDir;
        this.dockerContainerId = dockerContainerId;
        this.envInstanceUuid = envInstanceUuid;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Long getPipelineRunId() {
        return pipelineRunId;
    }

    public void setPipelineRunId(Long pipelineRunId) {
        this.pipelineRunId = pipelineRunId;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String getDockerHostWorkingDir() {
        return dockerHostWorkingDir;
    }

    public void setDockerHostWorkingDir(String dockerHostWorkingDir) {
        this.dockerHostWorkingDir = dockerHostWorkingDir;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getDockerContainerId() {
        return dockerContainerId;
    }

    public void setDockerContainerId(String dockerContainerId) {
        this.dockerContainerId = dockerContainerId;
    }

    public String getEnvInstanceUuid() {
        return envInstanceUuid;
    }

    public void setEnvInstanceUuid(String envInstanceUuid) {
        this.envInstanceUuid = envInstanceUuid;
    }
}
