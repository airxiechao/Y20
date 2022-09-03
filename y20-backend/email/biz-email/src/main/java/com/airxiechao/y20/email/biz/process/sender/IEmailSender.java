package com.airxiechao.y20.email.biz.process.sender;

public interface IEmailSender {

    void sendVerificationCode(String email, String code) throws Exception;
}
