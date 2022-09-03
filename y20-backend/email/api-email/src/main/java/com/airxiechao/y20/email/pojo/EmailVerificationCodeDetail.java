package com.airxiechao.y20.email.pojo;

import java.util.Date;

public class EmailVerificationCodeDetail {
    private String code;
    private String email;
    private Date expireTime;

    public EmailVerificationCodeDetail(String code, String email, Date expireTime) {
        this.code = code;
        this.email = email;
        this.expireTime = expireTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
