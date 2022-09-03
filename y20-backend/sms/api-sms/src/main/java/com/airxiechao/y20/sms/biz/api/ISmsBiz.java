package com.airxiechao.y20.sms.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;

@IBiz
public interface ISmsBiz {

    String sendVerificationCode(String mobile) throws Exception;
    boolean checkVerificationCode(String token, String mobile, String code) throws Exception;
}
