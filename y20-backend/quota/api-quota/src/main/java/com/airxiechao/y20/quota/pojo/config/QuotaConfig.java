package com.airxiechao.y20.quota.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("quota.yml")
public class QuotaConfig {
    private String name;
    private int port;
    private int freeQuotaNumAgent;
    private int freeQuotaNumPipelineRun;
    private int freeQuotaDays;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getFreeQuotaNumAgent() {
        return freeQuotaNumAgent;
    }

    public void setFreeQuotaNumAgent(int freeQuotaNumAgent) {
        this.freeQuotaNumAgent = freeQuotaNumAgent;
    }

    public int getFreeQuotaNumPipelineRun() {
        return freeQuotaNumPipelineRun;
    }

    public void setFreeQuotaNumPipelineRun(int freeQuotaNumPipelineRun) {
        this.freeQuotaNumPipelineRun = freeQuotaNumPipelineRun;
    }

    public int getFreeQuotaDays() {
        return freeQuotaDays;
    }

    public void setFreeQuotaDays(int freeQuotaDays) {
        this.freeQuotaDays = freeQuotaDays;
    }
}

