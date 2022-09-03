package com.airxiechao.y20.monitor.pojo;

public class ServiceMonitorTarget {
    private String namePattern;

    public ServiceMonitorTarget() {
    }

    public ServiceMonitorTarget(String namePattern) {
        this.namePattern = namePattern;
    }

    public String getNamePattern() {
        return namePattern;
    }

    public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
    }
}
