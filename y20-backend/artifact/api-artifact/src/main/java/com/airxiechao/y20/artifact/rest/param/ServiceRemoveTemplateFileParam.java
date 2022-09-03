package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceRemoveTemplateFileParam {
    @Required private Long templateId;

    public ServiceRemoveTemplateFileParam() {
    }

    public ServiceRemoveTemplateFileParam(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
