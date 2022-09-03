package com.airxiechao.y20.pipeline.pojo;

import com.airxiechao.y20.pipeline.db.record.PipelineStepTypeRecord;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class PipelineStepType {
    private String type;
    private String name;
    private String icon;
    private String description;
    private String category;
    private Integer order;
    private List<PipelineStepTypeParam> params;
    private List<String> outputs;
    private List<String> requires;

    public PipelineStepTypeRecord toRecord(){
        PipelineStepTypeRecord record = new PipelineStepTypeRecord();
        record.setType(this.type);
        record.setName(this.name);
        record.setIcon(this.icon);
        record.setDescription(this.description);
        record.setCategory(this.category);
        record.setOrder(this.order);
        record.setParams(JSON.toJSONString(this.params));
        record.setOutputs(JSON.toJSONString(this.outputs));
        record.setRequires(JSON.toJSONString(this.requires));

        return record;
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

    public List<PipelineStepTypeParam> getParams() {
        return params;
    }

    public void setParams(List<PipelineStepTypeParam> params) {
        this.params = params;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }
}
