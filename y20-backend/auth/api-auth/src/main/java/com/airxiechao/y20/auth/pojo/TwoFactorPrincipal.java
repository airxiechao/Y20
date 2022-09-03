package com.airxiechao.y20.auth.pojo;

import java.util.Date;

public class TwoFactorPrincipal {
    private String twoFactorUuid;
    private Long twoFactorUserId;
    private Date twoFactorBeginTime;
    private Date twoFactorEndTime;

    public String getTwoFactorUuid() {
        return twoFactorUuid;
    }

    public void setTwoFactorUuid(String twoFactorUuid) {
        this.twoFactorUuid = twoFactorUuid;
    }

    public Long getTwoFactorUserId() {
        return twoFactorUserId;
    }

    public void setTwoFactorUserId(Long twoFactorUserId) {
        this.twoFactorUserId = twoFactorUserId;
    }

    public Date getTwoFactorBeginTime() {
        return twoFactorBeginTime;
    }

    public void setTwoFactorBeginTime(Date twoFactorBeginTime) {
        this.twoFactorBeginTime = twoFactorBeginTime;
    }

    public Date getTwoFactorEndTime() {
        return twoFactorEndTime;
    }

    public void setTwoFactorEndTime(Date twoFactorEndTime) {
        this.twoFactorEndTime = twoFactorEndTime;
    }
}
