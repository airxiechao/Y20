package com.airxiechao.y20.pay.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class OrderQuotaQrParam {
    @Required private Long userId;
    @Required private String billingPlan;
    @Required private Integer numMonth;
    @Required String payType;

    public OrderQuotaQrParam() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(String billingPlan) {
        this.billingPlan = billingPlan;
    }

    public Integer getNumMonth() {
        return numMonth;
    }

    public void setNumMonth(Integer numMonth) {
        this.numMonth = numMonth;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
