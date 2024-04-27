package com.airxiechao.y20.activity.db.mongodb.document;

import com.airxiechao.axcboot.storage.db.mongodb.annotation.MongoDbCollection;

import java.util.Date;

@MongoDbCollection("activity_template")
public class TemplateActivityDocument {
    private Long userId;
    private Long templateId;
    private String type;
    private String param;
    private Date time;

    public TemplateActivityDocument() {
    }

    public TemplateActivityDocument(Long userId, Long templateId, String type, String param, Date time) {
        this.userId = userId;
        this.templateId = templateId;
        this.type = type;
        this.param = param;
        this.time = time;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
