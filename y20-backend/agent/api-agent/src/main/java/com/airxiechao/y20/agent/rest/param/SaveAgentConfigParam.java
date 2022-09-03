package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class SaveAgentConfigParam {
    @Required private Long userId;
    @Required private String agentId;
    @Required private String config;

    public SaveAgentConfigParam() {
    }

    public SaveAgentConfigParam(Long userId, String agentId, String config) {
        this.userId = userId;
        this.agentId = agentId;
        this.config = config;
    }

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

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
