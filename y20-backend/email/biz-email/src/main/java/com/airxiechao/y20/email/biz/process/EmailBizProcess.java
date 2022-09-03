package com.airxiechao.y20.email.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.y20.common.verification.VerificationUtil;
import com.airxiechao.y20.email.biz.api.IEmailBiz;
import com.airxiechao.y20.email.biz.process.sender.IEmailSender;
import com.airxiechao.y20.email.biz.process.sender.smtp.SmtpEmailSender;
import com.airxiechao.y20.email.pojo.EmailVerificationCodeDetail;
import com.airxiechao.y20.email.pojo.config.EmailConfig;
import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class EmailBizProcess implements IEmailBiz {

    private static final int EXPIRE_MINUTES = 5;
    private static final EmailConfig config = ConfigFactory.get(EmailConfig.class);
    private static final IEmailSender emailSender = new SmtpEmailSender();

    @Override
    public String sendVerificationCode(String email) throws Exception {
        String code = VerificationUtil.randomVerificationCode();

        emailSender.sendVerificationCode(email, code);

        Date expireTime = createExpireTime();
        EmailVerificationCodeDetail detail = new EmailVerificationCodeDetail(code, email, expireTime);
        String token = AesUtil.encryptByPBKDF2(config.getVerificationCodeTokenEncryptKey(), config.getVerificationCodeTokenEncryptKey(), JSON.toJSONString(detail));

        return token;
    }

    @Override
    public boolean checkVerificationCode(String token, String email, String code) throws Exception {
        EmailVerificationCodeDetail detail = JSON.parseObject(AesUtil.decryptByPBKDF2(config.getVerificationCodeTokenEncryptKey(), config.getVerificationCodeTokenEncryptKey(), token), EmailVerificationCodeDetail.class);

        return detail.getExpireTime().after(new Date()) && detail.getEmail().equals(email) && detail.getCode().equals(code);
    }

    private Date createExpireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, EXPIRE_MINUTES);
        return calendar.getTime();
    }

}
