package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeletePipelinePendingParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private Long pipelinePendingId;

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

    public Long getPipelinePendingId() {
        return pipelinePendingId;
    }

    public void setPipelinePendingId(Long pipelinePendingId) {
        this.pipelinePendingId = pipelinePendingId;
    }
}
