package com.airxiechao.y20.auth.pojo;

import com.alibaba.fastjson.annotation.JSONType;

import java.util.Date;

@JSONType(orders = { "uuid", "userId", "accessScope", "accessPolicy", "beginTime", "endTime" })
public class AccessPrincipal {

    private String uuid;
    private Long userId;
    private String accessScope;
    private AccessPolicy accessPolicy;
    private Date beginTime;
    private Date endTime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccessScope() {
        return accessScope;
    }

    public void setAccessScope(String accessScope) {
        this.accessScope = accessScope;
    }

    public AccessPolicy getAccessPolicy() {
        return accessPolicy;
    }

    public void setAccessPolicy(AccessPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
