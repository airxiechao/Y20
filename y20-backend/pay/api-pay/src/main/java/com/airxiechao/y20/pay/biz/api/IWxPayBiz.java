package com.airxiechao.y20.pay.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pay.pojo.vo.OrderQrVo;
import com.airxiechao.y20.pay.wxpay.NotifyHeader;

@IBiz
public interface IWxPayBiz {
    OrderQrVo orderQuotaQr(
            Long userId,
            String billingPlan,
            Integer numMonth
    ) throws Exception;

    void notifyQuotaPay(NotifyHeader header, String body) throws Exception;
}
