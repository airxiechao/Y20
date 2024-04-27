package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ListAgentMetricParam {
    @Required private Long userId;
    @Required private String agentId;

    public ListAgentMetricParam() {
    }

    public ListAgentMetricParam(Long userId, String agentId) {
        this.userId = userId;
        this.agentId = agentId;
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
}
