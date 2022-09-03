package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceDeleteProjectAllPipelineParam {

    @Required
    private Long projectId;

    public ServiceDeleteProjectAllPipelineParam() {
    }

    public ServiceDeleteProjectAllPipelineParam(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
