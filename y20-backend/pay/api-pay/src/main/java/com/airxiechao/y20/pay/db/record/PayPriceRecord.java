package com.airxiechao.y20.pay.db.record;

import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

@Table("pay_price")
@Index(fields = {"billingPlan"}, unique = true)
public class PayPriceRecord {
    private Long id;
    private String billingPlan;
    private Integer numAgent;
    private Integer numPipelineRun;
    private Integer price;

    public PayPriceRecord() {
    }

    public PayPriceRecord(String billingPlan, Integer numAgent, Integer numPipelineRun, Integer price) {
        this.billingPlan = billingPlan;
        this.numAgent = numAgent;
        this.numPipelineRun = numPipelineRun;
        this.price = price;
    }

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

    public Integer getNumAgent() {
        return numAgent;
    }

    public void setNumAgent(Integer numAgent) {
        this.numAgent = numAgent;
    }

    public Integer getNumPipelineRun() {
        return numPipelineRun;
    }

    public void setNumPipelineRun(Integer numPipelineRun) {
        this.numPipelineRun = numPipelineRun;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
