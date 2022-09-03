package com.airxiechao.y20.template.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetTemplateParam {
    @Required private Long templateId;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
