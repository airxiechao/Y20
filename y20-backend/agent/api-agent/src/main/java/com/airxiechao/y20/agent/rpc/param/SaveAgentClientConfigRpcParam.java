package com.airxiechao.y20.agent.rpc.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class SaveAgentClientConfigRpcParam {

    @Required private String config;

    public SaveAgentClientConfigRpcParam() {
    }

    public SaveAgentClientConfigRpcParam(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
