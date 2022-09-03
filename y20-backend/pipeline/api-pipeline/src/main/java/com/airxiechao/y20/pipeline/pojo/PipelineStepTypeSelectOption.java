package com.airxiechao.y20.pipeline.pojo;

public class PipelineStepTypeSelectOption {

    private String label;
    private String value;

    public PipelineStepTypeSelectOption(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
