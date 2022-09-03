package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CreateAccountTwoFactorSecretParam {
    @Required private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
