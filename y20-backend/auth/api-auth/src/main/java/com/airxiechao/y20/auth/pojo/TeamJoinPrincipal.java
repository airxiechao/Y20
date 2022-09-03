package com.airxiechao.y20.auth.pojo;

import java.util.Date;

public class TeamJoinPrincipal {
    private String teamJoinUuid;
    private Long teamJoinUserId;
    private Long teamJoinTeamId;
    private Date teamJoinBeginTime;
    private Date teamJoinEndTime;

    public String getTeamJoinUuid() {
        return teamJoinUuid;
    }

    public void setTeamJoinUuid(String teamJoinUuid) {
        this.teamJoinUuid = teamJoinUuid;
    }

    public Long getTeamJoinUserId() {
        return teamJoinUserId;
    }

    public void setTeamJoinUserId(Long teamJoinUserId) {
        this.teamJoinUserId = teamJoinUserId;
    }

    public Long getTeamJoinTeamId() {
        return teamJoinTeamId;
    }

    public void setTeamJoinTeamId(Long teamJoinTeamId) {
        this.teamJoinTeamId = teamJoinTeamId;
    }

    public Date getTeamJoinBeginTime() {
        return teamJoinBeginTime;
    }

    public void setTeamJoinBeginTime(Date teamJoinBeginTime) {
        this.teamJoinBeginTime = teamJoinBeginTime;
    }

    public Date getTeamJoinEndTime() {
        return teamJoinEndTime;
    }

    public void setTeamJoinEndTime(Date teamJoinEndTime) {
        this.teamJoinEndTime = teamJoinEndTime;
    }
}
