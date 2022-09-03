package com.airxiechao.y20.project.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeleteProjectVariableParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private String variableName;

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

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
}
