package com.airxiechao.y20.monitor.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("monitor.yml")
public class MonitorConfig {
    private String name;
    private int port;

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
}
