package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetTeamJoinTokenParam {
    @Required private Long userId;
    @Required private String joinTokenHashed;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJoinTokenHashed() {
        return joinTokenHashed;
    }

    public void setJoinTokenHashed(String joinTokenHashed) {
        this.joinTokenHashed = joinTokenHashed;
    }
}
