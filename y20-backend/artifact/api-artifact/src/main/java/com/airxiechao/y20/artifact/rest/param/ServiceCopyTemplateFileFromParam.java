package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceCopyTemplateFileFromParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long fromPipelineId;
    @Required private Long templateId;

    public ServiceCopyTemplateFileFromParam() {
    }

    public ServiceCopyTemplateFileFromParam(Long userId, Long projectId, Long fromPipelineId, Long templateId) {
        this.userId = userId;
        this.projectId = projectId;
        this.fromPipelineId = fromPipelineId;
        this.templateId = templateId;
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

    public Long getFromPipelineId() {
        return fromPipelineId;
    }

    public void setFromPipelineId(Long fromPipelineId) {
        this.fromPipelineId = fromPipelineId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
