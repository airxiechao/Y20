package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UninstallAgentParam {

    @Required private Long userId;
    @Required private String agentId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
