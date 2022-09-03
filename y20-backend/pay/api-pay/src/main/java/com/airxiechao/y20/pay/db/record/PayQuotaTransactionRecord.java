package com.airxiechao.y20.pay.db.record;

import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

import java.util.Date;

@Table("pay_quota_transaction")
@Index(fields = {"outTradeNo"}, unique = true)
@Index(fields = {"transactionId"}, unique = true)
@Index(fields = {"userId", "payTime"})
public class PayQuotaTransactionRecord {
    private Long id;
    private Long userId;
    private Integer numAgent;
    private Integer numAgentMonth;
    private Integer numPipelineRun;
    private Integer numPipelineRunMonth;
    private Integer total;
    private String payType;
    private String outTradeNo;
    private String transactionId;
    private Date createTime;
    private Date payTime;
    private String payer;
    private Integer payAmount;
    private Boolean payed;

    public PayQuotaTransactionRecord() {
    }

    public PayQuotaTransactionRecord(Long userId, Integer numAgent, Integer numAgentMonth, Integer numPipelineRun, Integer numPipelineRunMonth,
                                     Integer total, String payType, String outTradeNo, String transactionId) {
        this.userId = userId;
        this.numAgent = numAgent;
        this.numAgentMonth = numAgentMonth;
        this.numPipelineRun = numPipelineRun;
        this.numPipelineRunMonth = numPipelineRunMonth;
        this.total = total;
        this.payType = payType;
        this.outTradeNo = outTradeNo;
        this.transactionId = transactionId;
        this.createTime = new Date();
        this.payed = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getNumAgent() {
        return numAgent;
    }

    public void setNumAgent(Integer numAgent) {
        this.numAgent = numAgent;
    }

    public Integer getNumAgentMonth() {
        return numAgentMonth;
    }

    public void setNumAgentMonth(Integer numAgentMonth) {
        this.numAgentMonth = numAgentMonth;
    }

    public Integer getNumPipelineRun() {
        return numPipelineRun;
    }

    public void setNumPipelineRun(Integer numPipelineRun) {
        this.numPipelineRun = numPipelineRun;
    }

    public Integer getNumPipelineRunMonth() {
        return numPipelineRunMonth;
    }

    public void setNumPipelineRunMonth(Integer numPipelineRunMonth) {
        this.numPipelineRunMonth = numPipelineRunMonth;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }
}
