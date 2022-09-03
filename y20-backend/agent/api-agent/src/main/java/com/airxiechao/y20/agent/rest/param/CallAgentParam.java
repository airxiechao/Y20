package com.airxiechao.y20.agent.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CallAgentParam {

    @Required private String clientId;
    @Required private String interfaceName;
    @Required private String methodName;
    @Required private Object methodParam;

    public CallAgentParam(String clientId, String interfaceName, String methodName, Object methodParam) {
        this.clientId = clientId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.methodParam = methodParam;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(Object methodParam) {
        this.methodParam = methodParam;
    }

}
