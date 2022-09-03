package com.airxiechao.y20.email.biz.process.sender.smtp;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.email.biz.process.sender.IEmailSender;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

public class SmtpEmailSender implements IEmailSender {

    private static final SmtpEmailConfig config = ConfigFactory.get(SmtpEmailConfig.class);

    @Override
    public void sendVerificationCode(String email, String code) throws Exception {
        Email simpleEmail = new SimpleEmail();
        simpleEmail.setHostName(config.getSmtpHost());
        simpleEmail.setSmtpPort(config.getSmtpPort());
        simpleEmail.setAuthenticator(new DefaultAuthenticator(config.getUsername(), config.getPassword()));
        simpleEmail.setSSLOnConnect(true);
        simpleEmail.setFrom(config.getSender(), "Y20持续部署");
        simpleEmail.setSubject("邮箱验证码");
        simpleEmail.setMsg(String.format("验证码为：%s，请勿泄露。", code));
        simpleEmail.addTo(email);
        simpleEmail.send();
    }
}
