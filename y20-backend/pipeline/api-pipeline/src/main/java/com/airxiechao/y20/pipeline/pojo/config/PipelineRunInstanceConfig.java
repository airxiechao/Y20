package com.airxiechao.y20.pipeline.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("pipeline-run-instance.yml")
public class PipelineRunInstanceConfig {
    private String name;
    private int port;
    private Long explorerRunUploadMaxSpeed;

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

    public Long getExplorerRunUploadMaxSpeed() {
        return explorerRunUploadMaxSpeed;
    }

    public void setExplorerRunUploadMaxSpeed(Long explorerRunUploadMaxSpeed) {
        this.explorerRunUploadMaxSpeed = explorerRunUploadMaxSpeed;
    }
}
