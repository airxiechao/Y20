package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateAccountEmailParam {
    @Required private Long userId;
    @Required private String email;
    @Required private String verificationCodeToken;
    @Required private String verificationCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
