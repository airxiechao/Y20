package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceGetAccountParam {
    @Required private Long userId;

    public ServiceGetAccountParam() {
    }

    public ServiceGetAccountParam(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
