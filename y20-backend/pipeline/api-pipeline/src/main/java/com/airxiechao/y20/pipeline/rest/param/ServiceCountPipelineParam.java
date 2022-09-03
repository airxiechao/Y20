package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceCountPipelineParam {
    @Required private Long userId;
    @Required private Long projectId;

    public ServiceCountPipelineParam() {
    }

    public ServiceCountPipelineParam(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

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
}
