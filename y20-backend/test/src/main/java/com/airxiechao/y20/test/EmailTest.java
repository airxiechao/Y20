package com.airxiechao.y20.test;

import com.airxiechao.y20.email.biz.process.sender.IEmailSender;
import com.airxiechao.y20.email.biz.process.sender.smtp.SmtpEmailSender;

public class EmailTest {

    public static void main(String[] args) throws Exception {
        IEmailSender sender = new SmtpEmailSender();
    }
}
