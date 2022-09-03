package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class LoginByTwoFactorParam {

    @Required private String twoFactorToken;
    @Required private String twoFactorCode;

    public String getTwoFactorToken() {
        return twoFactorToken;
    }

    public void setTwoFactorToken(String twoFactorToken) {
        this.twoFactorToken = twoFactorToken;
    }

    public String getTwoFactorCode() {
        return twoFactorCode;
    }

    public void setTwoFactorCode(String twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }
}
