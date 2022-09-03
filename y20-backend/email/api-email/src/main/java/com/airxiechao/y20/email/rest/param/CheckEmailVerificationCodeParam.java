package com.airxiechao.y20.email.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CheckEmailVerificationCodeParam {

    @Required private String verificationCodeToken;
    @Required private String verificationEmail;
    @Required private String verificationCode;

    public CheckEmailVerificationCodeParam(String verificationCodeToken, String verificationEmail, String verificationCode) {
        this.verificationCodeToken = verificationCodeToken;
        this.verificationEmail = verificationEmail;
        this.verificationCode = verificationCode;
    }

    public String getVerificationCodeToken() {
        return verificationCodeToken;
    }

    public void setVerificationCodeToken(String verificationCodeToken) {
        this.verificationCodeToken = verificationCodeToken;
    }

    public String getVerificationEmail() {
        return verificationEmail;
    }

    public void setVerificationEmail(String verificationEmail) {
        this.verificationEmail = verificationEmail;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
