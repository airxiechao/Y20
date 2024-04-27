package com.airxiechao.y20.auth;

import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.common.core.biz.Biz;

import java.util.Calendar;
import java.util.Date;

public class TestToken {
    public static void main(String[] args){
        IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);

        Date begin = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(Calendar.YEAR, 10);
        Date end = cal.getTime();

        String token = accessTokenBiz.createAgentAccessToken(1L, "a-token", begin, end, "1237.0.0.1");
        System.out.println(token);
    }
}
