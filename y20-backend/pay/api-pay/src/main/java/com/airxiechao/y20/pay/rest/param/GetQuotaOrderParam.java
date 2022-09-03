package com.airxiechao.y20.pay.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class GetQuotaOrderParam {
    @Required private Long userId;
    @Required private String outTradeNo;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
