package com.airxiechao.y20.pipeline.pojo.vo;

import com.airxiechao.y20.pipeline.db.record.PipelineStepTypeRecord;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class PipelineStepTypeVo {
    private String type;
    private String name;
    private String icon;
    private String description;
    private String category;
    private Integer order;
    private List<String> outputs;

    public PipelineStepTypeVo() {
    }

    public PipelineStepTypeVo(PipelineStepTypeRecord record) {
        this.type = record.getType();
        this.name = record.getName();
        this.icon = record.getIcon();
        this.description = record.getDescription();
        this.category = record.getCategory();
        this.order = record.getOrder();
        this.outputs = record.toPojo().getOutputs();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }
}
