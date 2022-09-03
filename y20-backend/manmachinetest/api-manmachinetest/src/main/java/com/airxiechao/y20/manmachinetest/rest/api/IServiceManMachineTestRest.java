package com.airxiechao.y20.manmachinetest.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceManMachineTestRest {

    @Post("/service/question/check")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response checkQuestion(Object exc);
}
