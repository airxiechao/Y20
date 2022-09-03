package com.airxiechao.y20.test;

import com.airxiechao.y20.auth.biz.api.IAccessTokenBiz;
import com.airxiechao.y20.auth.biz.api.IUserBiz;
import com.airxiechao.y20.common.core.biz.Biz;

public class AuthTest {

    public static void main(String[] args) throws Exception {
        IUserBiz userBiz = Biz.get(IUserBiz.class);
        IAccessTokenBiz accessTokenBiz = Biz.get(IAccessTokenBiz.class);

        String serviceAccessToken = accessTokenBiz.createServiceAccessToken();
        System.out.println(serviceAccessToken);
    }
}
