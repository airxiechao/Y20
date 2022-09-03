package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class DeleteTeamMemberParam {
    @Required private Long userId;
    @Required private Long memberUserId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(Long memberUserId) {
        this.memberUserId = memberUserId;
    }
}
