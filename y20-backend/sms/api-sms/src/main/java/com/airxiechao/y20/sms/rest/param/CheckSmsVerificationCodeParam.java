package com.airxiechao.y20.sms.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CheckSmsVerificationCodeParam {

    @Required private String verificationCodeToken;
    @Required private String verificationMobile;
    @Required private String verificationCode;

    public CheckSmsVerificationCodeParam(String verificationCodeToken, String verificationMobile, String verificationCode) {
        this.verificationCodeToken = verificationCodeToken;
        this.verificationMobile = verificationMobile;
        this.verificationCode = verificationCode;
    }

    public String getVerificationCodeToken() {
        return verificationCodeToken;
    }

    public void setVerificationCodeToken(String verificationCodeToken) {
        this.verificationCodeToken = verificationCodeToken;
    }

    public String getVerificationMobile() {
        return verificationMobile;
    }

    public void setVerificationMobile(String verificationMobile) {
        this.verificationMobile = verificationMobile;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
