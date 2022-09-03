package com.airxiechao.y20.email.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceEmailRest {

    @Post("/service/email/verificationcode/check")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response checkVerificationCode(Object exc);
}
