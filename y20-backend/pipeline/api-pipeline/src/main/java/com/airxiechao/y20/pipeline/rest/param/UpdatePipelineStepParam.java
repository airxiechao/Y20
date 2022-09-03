package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;

public class UpdatePipelineStepParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private PipelineStep step;
    @Required private Integer position;

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

    public PipelineStep getStep() {
        return step;
    }

    public void setStep(PipelineStep step) {
        this.step = step;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
