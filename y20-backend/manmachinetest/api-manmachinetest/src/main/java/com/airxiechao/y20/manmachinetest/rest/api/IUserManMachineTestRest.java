package com.airxiechao.y20.manmachinetest.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;

@IRest
public interface IUserManMachineTestRest {

    @Get("/user/question/next")
    Response nextQuestion(Object exc);

    @Post("/user/question/check")
    Response checkQuestion(Object exc);
}
