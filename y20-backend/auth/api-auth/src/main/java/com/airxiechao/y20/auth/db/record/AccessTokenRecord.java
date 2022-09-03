package com.airxiechao.y20.auth.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.auth.pojo.AccessPolicy;
import com.alibaba.fastjson.JSON;

import java.util.Date;

@Table("access_token")
@Index(fields = {"userId", "scope"})
@Index(fields = {"tokenHashed"}, unique = true)
public class AccessTokenRecord {

    private Long id;
    private Long userId;
    @Column(length = 50) private String name;
    @Column(length = 100) private String tokenHashed;
    @Column(length = 50) private String scope;
    @Column(length = 1000) private String policy;
    private Date beginTime;
    private Date endTime;
    private Date createTime;
    @Column(length = 50) private String createIp;
    @Column(length = 50) private String createLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenHashed() {
        return tokenHashed;
    }

    public void setTokenHashed(String tokenHashed) {
        this.tokenHashed = tokenHashed;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setPolicy(AccessPolicy policy) {
        this.policy = JSON.toJSONString(policy);
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
}
