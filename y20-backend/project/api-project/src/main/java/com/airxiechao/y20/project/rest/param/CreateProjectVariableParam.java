package com.airxiechao.y20.project.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;

public class CreateProjectVariableParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private PipelineVariable variable;

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

    public PipelineVariable getVariable() {
        return variable;
    }

    public void setVariable(PipelineVariable variable) {
        this.variable = variable;
    }
}
