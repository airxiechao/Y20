package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserAuthRest {

    @Post("/user/login/username")
    Response loginByUsername(Object exc);

    @Post("/user/login/mobile")
    Response loginByMobile(Object exc);

    @Post("/user/login/twofactor")
    Response loginByTwoFactor(Object exc);

    @Post("/user/logout")
    Response logout(Object exc);

    @Post("/user/signup")
    Response signup(Object exc);

    // access token

    @Get("/user/access/token/user/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listUserAccessToken(Object exc);

    @Get("/user/access/token/notuser/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listNotUserAccessToken(Object exc);

    @Post("/user/access/token/agent/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createAgentAccessToken(Object exc);

    @Post("/user/access/token/webhook/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createWebhookAccessToken(Object exc);

    @Post("/user/access/token/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deleteAccessToken(Object exc);
}
