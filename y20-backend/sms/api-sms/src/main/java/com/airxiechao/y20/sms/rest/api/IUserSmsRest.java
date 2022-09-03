package com.airxiechao.y20.sms.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;

@IRest
public interface IUserSmsRest {

    @Post("/user/sms/verificationcode/send")
    Response sendVerificationCode(Object exc);

}
