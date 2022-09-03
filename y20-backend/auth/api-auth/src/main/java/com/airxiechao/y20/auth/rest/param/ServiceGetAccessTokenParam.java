package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceGetAccessTokenParam {
    @Required private String accessToken;

    public ServiceGetAccessTokenParam() {
    }

    public ServiceGetAccessTokenParam(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
