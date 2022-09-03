package com.airxiechao.y20.pay.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pay.pojo.vo.OrderPageVo;

import java.util.Map;

@IBiz
public interface IAliPayBiz {
    OrderPageVo orderQuotaPage(
            Long userId,
            String billingPlan,
            Integer numMonth
    ) throws Exception;

    void notifyQuotaPay(Map<String, String> params) throws Exception;
}
