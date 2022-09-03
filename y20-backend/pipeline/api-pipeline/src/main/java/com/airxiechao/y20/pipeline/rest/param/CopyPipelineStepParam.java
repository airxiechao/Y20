package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CopyPipelineStepParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private Integer srcPosition;
    @Required private Integer destPosition;

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

    public Integer getSrcPosition() {
        return srcPosition;
    }

    public void setSrcPosition(Integer srcPosition) {
        this.srcPosition = srcPosition;
    }

    public Integer getDestPosition() {
        return destPosition;
    }

    public void setDestPosition(Integer destPosition) {
        this.destPosition = destPosition;
    }
}
