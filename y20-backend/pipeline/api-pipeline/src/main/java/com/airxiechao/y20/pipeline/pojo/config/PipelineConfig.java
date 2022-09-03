package com.airxiechao.y20.pipeline.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("pipeline.yml")
public class PipelineConfig {
    private String name;
    private int port;
    private Boolean enableQuota;

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

    public Boolean getEnableQuota() {
        return enableQuota;
    }

    public void setEnableQuota(Boolean enableQuota) {
        this.enableQuota = enableQuota;
    }
}
