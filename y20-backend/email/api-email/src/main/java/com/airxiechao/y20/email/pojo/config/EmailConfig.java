package com.airxiechao.y20.email.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("email.yml")
public class EmailConfig {
    private String name;
    private int port;
    private String verificationCodeTokenEncryptKey;

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

    public String getVerificationCodeTokenEncryptKey() {
        return verificationCodeTokenEncryptKey;
    }

    public void setVerificationCodeTokenEncryptKey(String verificationCodeTokenEncryptKey) {
        this.verificationCodeTokenEncryptKey = verificationCodeTokenEncryptKey;
    }
}
