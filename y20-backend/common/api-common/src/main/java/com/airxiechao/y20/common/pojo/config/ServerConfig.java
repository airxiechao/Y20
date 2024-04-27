package com.airxiechao.y20.common.pojo.config;

public class ServerConfig {
    private String url;
    private Boolean enableMigrate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEnableMigrate() {
        return enableMigrate;
    }

    public void setEnableMigrate(Boolean enableMigrate) {
        this.enableMigrate = enableMigrate;
    }
}
