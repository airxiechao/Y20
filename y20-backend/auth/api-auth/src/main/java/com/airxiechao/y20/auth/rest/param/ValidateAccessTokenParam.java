package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ValidateAccessTokenParam {

    @Required private String accessToken;
    private String scope;
    private String item;
    private Integer mode;

    public ValidateAccessTokenParam(String accessToken, String scope, String item, Integer mode) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.item = item;
        this.mode = mode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}
