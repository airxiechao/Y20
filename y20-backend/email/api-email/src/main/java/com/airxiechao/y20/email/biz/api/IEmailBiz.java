package com.airxiechao.y20.email.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;

@IBiz
public interface IEmailBiz {

    String sendVerificationCode(String email) throws Exception;
    boolean checkVerificationCode(String token, String email, String code) throws Exception;
}
