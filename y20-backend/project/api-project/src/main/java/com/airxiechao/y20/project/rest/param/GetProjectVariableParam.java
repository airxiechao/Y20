package com.airxiechao.y20.project.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetProjectVariableParam {

    @Required private Long userId;
    @Required private Long projectId;
    @Required private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
