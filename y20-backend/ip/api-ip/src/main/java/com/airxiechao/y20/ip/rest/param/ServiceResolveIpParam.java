package com.airxiechao.y20.ip.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceResolveIpParam {
    @Required private String ip;

    public ServiceResolveIpParam() {
    }

    public ServiceResolveIpParam(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
