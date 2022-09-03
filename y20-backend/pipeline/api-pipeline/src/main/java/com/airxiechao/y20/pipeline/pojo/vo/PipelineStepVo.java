package com.airxiechao.y20.pipeline.pojo.vo;

public class PipelineStepVo {
    private String type;
    private String typeName;
    private String name;

    private String condition;
    private Boolean disabled;

    public PipelineStepVo(String type, String typeName, String name, String condition, Boolean disabled) {
        this.type = type;
        this.typeName = typeName;
        this.name = name;
        this.condition = condition;
        this.disabled = disabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
