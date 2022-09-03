package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UploadPipelineRunFileParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private Long pipelineRunId;
    private String dir;
    @Required private FileData file;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public FileData getFile() {
        return file;
    }

    public void setFile(FileData file) {
        this.file = file;
    }
}
