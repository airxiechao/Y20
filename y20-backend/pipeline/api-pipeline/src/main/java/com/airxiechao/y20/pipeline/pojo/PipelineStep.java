package com.airxiechao.y20.pipeline.pojo;

import java.util.HashMap;
import java.util.Map;

public class PipelineStep {

    private String type;
    private String name;
    private Boolean disabled;
    private String condition;
    private Map<String, Object> params = new HashMap<>();

    public String getType() {
        return type;
    }

    public PipelineStep setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public PipelineStep setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Object getParam(String name){
        return this.params.get(name);
    }

    public Object getParam(String name, Object defaultValue){
        return this.params.getOrDefault(name, defaultValue);
    }

    public PipelineStep setParam(String name, Object value){
        this.params.put(name, value);
        return this;
    }
}
