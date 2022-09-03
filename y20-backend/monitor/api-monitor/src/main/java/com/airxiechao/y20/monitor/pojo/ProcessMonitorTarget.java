package com.airxiechao.y20.monitor.pojo;

public class ProcessMonitorTarget {
    private String commandPattern;

    public ProcessMonitorTarget() {
    }

    public ProcessMonitorTarget(String commandPattern) {
        this.commandPattern = commandPattern;
    }

    public String getCommandPattern() {
        return commandPattern;
    }

    public void setCommandPattern(String commandPattern) {
        this.commandPattern = commandPattern;
    }
}
