package com.airxiechao.y20.agent.rpc.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetRestServicePathRpcParam {
    @Required private String className;

    public GetRestServicePathRpcParam() {
    }

    public GetRestServicePathRpcParam(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
