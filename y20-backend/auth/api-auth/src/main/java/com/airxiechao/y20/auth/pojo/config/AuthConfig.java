package com.airxiechao.y20.auth.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("auth.yml")
public class AuthConfig {

    private String name;
    private int port;
    private String accessTokenEncryptKey;
    private int numMinuteOfUserAccessToken;
    private boolean enableSmsVerificationCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccessTokenEncryptKey() {
        return accessTokenEncryptKey;
    }

    public void setAccessTokenEncryptKey(String accessTokenEncryptKey) {
        this.accessTokenEncryptKey = accessTokenEncryptKey;
    }

    public int getNumMinuteOfUserAccessToken() {
        return numMinuteOfUserAccessToken;
    }

    public void setNumMinuteOfUserAccessToken(int numMinuteOfUserAccessToken) {
        this.numMinuteOfUserAccessToken = numMinuteOfUserAccessToken;
    }

    public boolean isEnableSmsVerificationCode() {
        return enableSmsVerificationCode;
    }

    public void setEnableSmsVerificationCode(boolean enableSmsVerificationCode) {
        this.enableSmsVerificationCode = enableSmsVerificationCode;
    }
}
