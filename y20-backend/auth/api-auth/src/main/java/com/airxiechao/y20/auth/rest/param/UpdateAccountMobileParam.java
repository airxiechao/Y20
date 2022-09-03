package com.airxiechao.y20.auth.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class UpdateAccountMobileParam {
    @Required private Long userId;
    @Required private String mobile;
    @Required private String verificationCodeToken;
    @Required private String verificationCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
