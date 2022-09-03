package com.airxiechao.y20.pay.db.record;

import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

@Table("pay_discount")
@Index(fields = {"billingPlan"})
public class PayDiscountRecord {
    private Long id;
    private String billingPlan;
    private Integer numMin;
    private Integer numMax;
    private Double rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(String billingPlan) {
        this.billingPlan = billingPlan;
    }

    public Integer getNumMin() {
        return numMin;
    }

    public void setNumMin(Integer numMin) {
        this.numMin = numMin;
    }

    public Integer getNumMax() {
        return numMax;
    }

    public void setNumMax(Integer numMax) {
        this.numMax = numMax;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
