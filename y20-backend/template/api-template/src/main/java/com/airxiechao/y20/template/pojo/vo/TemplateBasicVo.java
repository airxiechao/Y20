package com.airxiechao.y20.template.pojo.vo;

import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.template.db.record.TemplateRecord;

import java.util.*;

public class TemplateBasicVo {
    private Long templateId;
    private Long userId;
    private String username;
    private String name;
    private String shortDescription;
    private Date createTime;
    private Date lastUpdateTime;

    public TemplateBasicVo() {
    }

    public TemplateBasicVo(TemplateRecord record) {
        this.templateId = record.getId();
        this.userId = record.getUserId();
        this.username = record.getUsername();
        this.name = record.getName();
        String description = record.getDescription();
        if(!StringUtil.isBlank(description)) {
            int maxLength = 150;
            if(description.length() > maxLength){
                this.shortDescription = description.substring(0, maxLength) + "...";
            }else{
                this.shortDescription = description;
            }
        }
        this.createTime = record.getCreateTime();
        this.lastUpdateTime = record.getLastUpdateTime();
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
