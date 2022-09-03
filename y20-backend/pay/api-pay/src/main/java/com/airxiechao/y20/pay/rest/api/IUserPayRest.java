package com.airxiechao.y20.pay.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserPayRest {

    @Get("/user/pay/quota/price")
    //@Auth(scope = EnumAccessScope.USER)
    Response getQuotaPrice(Object exc);

    @Post("/user/pay/quota/order")
    @Auth(scope = EnumAccessScope.USER)
    Response orderQuota(Object exc);

    @Get("/user/pay/quota/order/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getQuotaOrder(Object exc);

}
