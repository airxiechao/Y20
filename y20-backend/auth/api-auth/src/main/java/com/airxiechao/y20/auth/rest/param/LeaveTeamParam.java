package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class LeaveTeamParam {
    @Required private Long userId;
    @Required private Long leaveTeamId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getLeaveTeamId() {
        return leaveTeamId;
    }

    public void setLeaveTeamId(Long leaveTeamId) {
        this.leaveTeamId = leaveTeamId;
    }
}
