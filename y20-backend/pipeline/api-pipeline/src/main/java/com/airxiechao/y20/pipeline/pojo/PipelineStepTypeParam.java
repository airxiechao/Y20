package com.airxiechao.y20.pipeline.pojo;

import java.util.List;

public class PipelineStepTypeParam {
    private String name;
    private String displayName;
    private int displayOrder;
    private String hint;
    private String type;
    private Boolean required;
    private String conditionalOnRequiredTrue;
    private String defaultValue;
    private List<PipelineStepTypeSelectOption> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getConditionalOnRequiredTrue() {
        return conditionalOnRequiredTrue;
    }

    public void setConditionalOnRequiredTrue(String conditionalOnRequiredTrue) {
        this.conditionalOnRequiredTrue = conditionalOnRequiredTrue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<PipelineStepTypeSelectOption> getOptions() {
        return options;
    }

    public void setOptions(List<PipelineStepTypeSelectOption> options) {
        this.options = options;
    }
}
