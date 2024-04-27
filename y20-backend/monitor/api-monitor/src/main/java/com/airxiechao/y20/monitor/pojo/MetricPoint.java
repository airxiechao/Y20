package com.airxiechao.y20.monitor.pojo;

public class MetricPoint {
    private Object value;
    private Long timestamp;

    public MetricPoint(Object value, Long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
