package com.airxiechao.y20.auth.pojo.vo;

import com.airxiechao.y20.auth.db.record.UserRecord;

public class AccountVo {
    private Long userId;
    private String username;
    private String mobile;
    private String email;
    private Boolean flagTwoFactor;

    public AccountVo() {
    }

    public AccountVo(Long userId, String username, String mobile, String email, Boolean flagTwoFactor) {
        this.userId = userId;
        this.username = username;
        this.mobile = mobile;
        this.email = email;
        this.flagTwoFactor = flagTwoFactor;
    }

    public AccountVo(UserRecord userRecord) {
        this.userId = userRecord.getId();
        this.username = userRecord.getUsername();
        this.mobile = userRecord.getMobile();
        this.email = userRecord.getEmail();
        this.flagTwoFactor = userRecord.getFlagTwoFactor();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getFlagTwoFactor() {
        return flagTwoFactor;
    }

    public void setFlagTwoFactor(Boolean flagTwoFactor) {
        this.flagTwoFactor = flagTwoFactor;
    }
}
