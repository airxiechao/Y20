package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class LoginByMobileParam {

    @Required private String mobile;
    @Required private String verificationCodeToken;
    @Required private String verificationCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerificationCodeToken() {
        return verificationCodeToken;
    }

    public void setVerificationCodeToken(String verificationCodeToken) {
        this.verificationCodeToken = verificationCodeToken;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
