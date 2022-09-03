package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserAccountRest {

    @Get("/user/account/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getAccount(Object exc);

    @Post("/user/account/mobile/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateAccountMobile(Object exc);

    @Post("/user/account/email/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateAccountEmail(Object exc);

    @Post("/user/account/password/update")
    @Auth(scope = EnumAccessScope.USER)
    Response updateAccountPassword(Object exc);

    @Post("/user/account/twofactor/secret/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createAccountTwoFactorSecret(Object exc);

    @Post("/user/account/twofactor/enable")
    @Auth(scope = EnumAccessScope.USER)
    Response enableAccountTwoFactor(Object exc);

    @Post("/user/account/twofactor/disable")
    @Auth(scope = EnumAccessScope.USER)
    Response disableAccountTwoFactor(Object exc);

}
