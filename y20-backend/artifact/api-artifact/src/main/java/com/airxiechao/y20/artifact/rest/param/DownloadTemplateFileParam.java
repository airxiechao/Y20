package com.airxiechao.y20.artifact.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DownloadTemplateFileParam {
    @Required private Long templateId;
    @Required private String path;

    public DownloadTemplateFileParam() {
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
