package com.airxiechao.y20.pay.rest.api;

import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.pay.wxpay.NotifyResp;

@IRest
public interface IServicePayRest {

    @Post("/service/pay/quota/notify/wxpay")
    NotifyResp wxpayNotifyQuotaPay(Object exc);

    @Post("/service/pay/quota/notify/alipay")
    void alipayNotifyQuotaPay(Object exc);
}
