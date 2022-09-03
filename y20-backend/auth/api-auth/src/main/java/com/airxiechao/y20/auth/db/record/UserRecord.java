package com.airxiechao.y20.auth.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

import java.util.Date;

@Table("user")
@Index(fields = {"username"}, unique = true)
@Index(fields = {"mobile"}, unique = true)
@Index(fields = {"email"}, unique = true)
public class UserRecord {

    private Long id;
    @Column(length = 50) private String username;
    @Column(length = 100) private String passwordHashed;
    @Column(length = 20) private String mobile;
    @Column(length = 50) private String email;
    private Boolean flagTwoFactor;
    @Column(length = 50) private String twoFactorSecret;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
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

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
