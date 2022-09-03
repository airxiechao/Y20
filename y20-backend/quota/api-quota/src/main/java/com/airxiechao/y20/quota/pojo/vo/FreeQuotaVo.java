package com.airxiechao.y20.quota.pojo.vo;

public class FreeQuotaVo {
    private Integer freeQuotaNumAgent;
    private Integer freeQuotaNumPipelineRun;

    public FreeQuotaVo() {
    }

    public FreeQuotaVo(Integer freeQuotaNumAgent, Integer freeQuotaNumPipelineRun) {
        this.freeQuotaNumAgent = freeQuotaNumAgent;
        this.freeQuotaNumPipelineRun = freeQuotaNumPipelineRun;
    }

    public Integer getFreeQuotaNumAgent() {
        return freeQuotaNumAgent;
    }

    public void setFreeQuotaNumAgent(Integer freeQuotaNumAgent) {
        this.freeQuotaNumAgent = freeQuotaNumAgent;
    }

    public Integer getFreeQuotaNumPipelineRun() {
        return freeQuotaNumPipelineRun;
    }

    public void setFreeQuotaNumPipelineRun(Integer freeQuotaNumPipelineRun) {
        this.freeQuotaNumPipelineRun = freeQuotaNumPipelineRun;
    }
}
