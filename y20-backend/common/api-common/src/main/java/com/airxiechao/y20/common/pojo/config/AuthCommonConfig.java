package com.airxiechao.y20.common.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("auth-common.yml")
public class AuthCommonConfig {

    private String serviceAccessToken;

    public String getServiceAccessToken() {
        return serviceAccessToken;
    }

    public void setServiceAccessToken(String serviceAccessToken) {
        this.serviceAccessToken = serviceAccessToken;
    }
}
