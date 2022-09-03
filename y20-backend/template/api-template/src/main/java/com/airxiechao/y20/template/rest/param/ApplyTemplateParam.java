package com.airxiechao.y20.template.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ApplyTemplateParam {
    @Required private Long userId;
    @Required private Long templateId;
    @Required private Long toProjectId;
    @Required private String name;
    @Required private Boolean flagOneRun;

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

    public Long getToProjectId() {
        return toProjectId;
    }

    public void setToProjectId(Long toProjectId) {
        this.toProjectId = toProjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFlagOneRun() {
        return flagOneRun;
    }

    public void setFlagOneRun(Boolean flagOneRun) {
        this.flagOneRun = flagOneRun;
    }
}
