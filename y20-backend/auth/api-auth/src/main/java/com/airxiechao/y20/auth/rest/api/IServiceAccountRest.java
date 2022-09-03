package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.auth.pojo.vo.AccountVo;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceAccountRest {

    @Get("/service/account/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<AccountVo> getAccount(Object exc);

}
