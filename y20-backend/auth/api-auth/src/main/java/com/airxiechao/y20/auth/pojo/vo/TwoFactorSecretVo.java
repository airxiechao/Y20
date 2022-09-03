package com.airxiechao.y20.auth.pojo.vo;

public class TwoFactorSecretVo {
    private String username;
    private String secret;

    public TwoFactorSecretVo() {
    }

    public TwoFactorSecretVo(String username, String secret) {
        this.username = username;
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
