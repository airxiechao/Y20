package com.airxiechao.y20.template.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeleteTemplateParam {
    @Required private Long userId;
    @Required private Long templateId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
