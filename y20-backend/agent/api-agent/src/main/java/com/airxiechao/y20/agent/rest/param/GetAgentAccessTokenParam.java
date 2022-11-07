package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetAgentAccessTokenParam {
    @Required private Long userId;
    @Required private String agentAccessToken;

    public GetAgentAccessTokenParam() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAgentAccessToken() {
        return agentAccessToken;
    }

    public void setAgentAccessToken(String agentAccessToken) {
        this.agentAccessToken = agentAccessToken;
    }
}
