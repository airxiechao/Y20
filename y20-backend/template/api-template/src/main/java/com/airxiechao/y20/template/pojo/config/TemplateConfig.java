package com.airxiechao.y20.template.pojo.config;


import com.airxiechao.axcboot.config.annotation.Config;

@Config("template.yml")
public class TemplateConfig {
    private String name;
    private int port;
    private String templateIndexDir;

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

    public String getTemplateIndexDir() {
        return templateIndexDir;
    }

    public void setTemplateIndexDir(String templateIndexDir) {
        this.templateIndexDir = templateIndexDir;
    }
}