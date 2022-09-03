package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdatePipelineStepConditionParam {

    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long pipelineId;
    @Required private Integer position;
    private String condition;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
