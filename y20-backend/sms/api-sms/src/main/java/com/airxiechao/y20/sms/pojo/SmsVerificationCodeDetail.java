package com.airxiechao.y20.sms.pojo;

import java.util.Date;

public class SmsVerificationCodeDetail {
    private String code;
    private String mobile;
    private Date expireTime;

    public SmsVerificationCodeDetail(String code, String mobile, Date expireTime) {
        this.code = code;
        this.mobile = mobile;
        this.expireTime = expireTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
