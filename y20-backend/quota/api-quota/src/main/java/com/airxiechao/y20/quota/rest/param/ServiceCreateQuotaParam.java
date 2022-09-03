package com.airxiechao.y20.quota.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceCreateQuotaParam {
    @Required private Long userId;
    @Required private Integer numAgent;
    @Required private Integer numAgentMonth;
    @Required private Integer numPipelineRun;
    @Required private Integer numPipelineRunMonth;
    @Required private Long payQuotaTransactionId;

    public ServiceCreateQuotaParam() {
    }

    public ServiceCreateQuotaParam(Long userId, Integer numAgent, Integer numAgentMonth, Integer numPipelineRun, Integer numPipelineRunMonth, Long payQuotaTransactionId) {
        this.userId = userId;
        this.numAgent = numAgent;
        this.numAgentMonth = numAgentMonth;
        this.numPipelineRun = numPipelineRun;
        this.numPipelineRunMonth = numPipelineRunMonth;
        this.payQuotaTransactionId = payQuotaTransactionId;
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

    public Long getPayQuotaTransactionId() {
        return payQuotaTransactionId;
    }

    public void setPayQuotaTransactionId(Long payQuotaTransactionId) {
        this.payQuotaTransactionId = payQuotaTransactionId;
    }
}
