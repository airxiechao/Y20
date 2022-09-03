package com.airxiechao.y20.auth.pojo.vo;

import com.airxiechao.y20.auth.db.record.AccessTokenRecord;

import java.util.Date;

public class AccessTokenVo {
    private Long id;
    private String name;
    private String scope;
    private Date beginTime;
    private Date endTime;
    private Date createTime;
    private String createIp;
    private String createLocation;
    private boolean flagCurrentSession;

    public AccessTokenVo(AccessTokenRecord record) {
        this.id = record.getId();
        this.name = record.getName();
        this.scope = record.getScope();
        this.beginTime = record.getBeginTime();
        this.endTime = record.getEndTime();
        this.createTime = record.getCreateTime();
        this.createIp = record.getCreateIp();
        this.createLocation = record.getCreateLocation();
        this.flagCurrentSession = false;
    }

    public AccessTokenVo(AccessTokenRecord record, boolean flagCurrentSession) {
        this.id = record.getId();
        this.name = record.getName();
        this.scope = record.getScope();
        this.beginTime = record.getBeginTime();
        this.endTime = record.getEndTime();
        this.createTime = record.getCreateTime();
        this.createIp = record.getCreateIp();
        this.createLocation = record.getCreateLocation();
        this.flagCurrentSession = flagCurrentSession;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getCreateLocation() {
        return createLocation;
    }

    public void setCreateLocation(String createLocation) {
        this.createLocation = createLocation;
    }

    public boolean isFlagCurrentSession() {
        return flagCurrentSession;
    }

    public void setFlagCurrentSession(boolean flagCurrentSession) {
        this.flagCurrentSession = flagCurrentSession;
    }
}
