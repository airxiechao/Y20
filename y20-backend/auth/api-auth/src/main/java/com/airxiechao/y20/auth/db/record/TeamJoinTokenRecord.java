package com.airxiechao.y20.auth.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

import java.util.Date;

@Table("team_join_token")
@Index(fields = {"userId", "teamId"})
@Index(fields = {"joinTokenHashed"}, unique = true)
public class TeamJoinTokenRecord {
    private Long id;
    private Long userId;
    private Long teamId;
    @Column(length = 100) private String joinTokenHashed;
    private Date beginTime;
    private Date endTime;
    private Date createTime;

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

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getJoinTokenHashed() {
        return joinTokenHashed;
    }

    public void setJoinTokenHashed(String joinTokenHashed) {
        this.joinTokenHashed = joinTokenHashed;
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
}
