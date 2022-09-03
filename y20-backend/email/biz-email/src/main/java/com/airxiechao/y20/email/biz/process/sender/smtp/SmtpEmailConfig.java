package com.airxiechao.y20.email.biz.process.sender.smtp;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("email-smtp.yml")
public class SmtpEmailConfig {

    private String smtpHost;
    private Integer smtpPort;
    private String username;
    private String password;
    private String sender;

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
