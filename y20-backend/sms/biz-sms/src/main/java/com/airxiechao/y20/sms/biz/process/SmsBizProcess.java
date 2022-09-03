package com.airxiechao.y20.sms.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.y20.common.verification.VerificationUtil;
import com.airxiechao.y20.sms.biz.api.ISmsBiz;
import com.airxiechao.y20.sms.biz.process.sender.ISmsSender;
import com.airxiechao.y20.sms.biz.process.sender.tencent.TencentSmsSender;
import com.airxiechao.y20.sms.pojo.SmsVerificationCodeDetail;
import com.airxiechao.y20.sms.pojo.config.SmsConfig;
import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SmsBizProcess implements ISmsBiz {

    private static final int EXPIRE_MINUTES = 5;
    private static final SmsConfig config = ConfigFactory.get(SmsConfig.class);
    private static final ISmsSender smsSender = new TencentSmsSender();

    @Override
    public String sendVerificationCode(String mobile) throws Exception {
        String code = VerificationUtil.randomVerificationCode();

        smsSender.sendVerificationCode(mobile, code);

        Date expireTime = createExpireTime();
        SmsVerificationCodeDetail detail = new SmsVerificationCodeDetail(code, mobile, expireTime);
        String token = AesUtil.encryptByPBKDF2(config.getVerificationCodeTokenEncryptKey(), config.getVerificationCodeTokenEncryptKey(), JSON.toJSONString(detail));

        return token;
    }

    @Override
    public boolean checkVerificationCode(String token, String mobile, String code) throws Exception {
        SmsVerificationCodeDetail detail = JSON.parseObject(AesUtil.decryptByPBKDF2(config.getVerificationCodeTokenEncryptKey(), config.getVerificationCodeTokenEncryptKey(), token), SmsVerificationCodeDetail.class);

        return detail.getExpireTime().after(new Date()) && detail.getMobile().equals(mobile) && detail.getCode().equals(code);
    }

    private Date createExpireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, EXPIRE_MINUTES);
        return calendar.getTime();
    }

}
