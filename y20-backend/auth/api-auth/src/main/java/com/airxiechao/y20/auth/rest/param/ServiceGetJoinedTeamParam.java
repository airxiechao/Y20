package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceGetJoinedTeamParam {

    @Required private Long memberUserId;
    @Required private Long joinedTeamId;

    public ServiceGetJoinedTeamParam() {
    }

    public ServiceGetJoinedTeamParam(Long memberUserId, Long joinedTeamId) {
        this.memberUserId = memberUserId;
        this.joinedTeamId = joinedTeamId;
    }

    public Long getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(Long memberUserId) {
        this.memberUserId = memberUserId;
    }

    public Long getJoinedTeamId() {
        return joinedTeamId;
    }

    public void setJoinedTeamId(Long joinedTeamId) {
        this.joinedTeamId = joinedTeamId;
    }
}
