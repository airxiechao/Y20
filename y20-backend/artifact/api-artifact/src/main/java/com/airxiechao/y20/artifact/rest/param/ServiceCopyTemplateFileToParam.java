package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceCopyTemplateFileToParam {
    @Required private Long userId;
    @Required private Long projectId;
    @Required private Long toPipelineId;
    @Required private Long templateId;

    public ServiceCopyTemplateFileToParam() {
    }

    public ServiceCopyTemplateFileToParam(Long userId, Long projectId, Long toPipelineId, Long templateId) {
        this.userId = userId;
        this.projectId = projectId;
        this.toPipelineId = toPipelineId;
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

    public Long getToPipelineId() {
        return toPipelineId;
    }

    public void setToPipelineId(Long toPipelineId) {
        this.toPipelineId = toPipelineId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
