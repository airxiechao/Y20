package com.airxiechao.y20.pipeline.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.PipelineStepType;
import com.airxiechao.y20.pipeline.pojo.PipelineStepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;
import com.alibaba.fastjson.JSON;

import java.util.List;

@Table("pipeline_step_type")
@Index(fields = {"type"}, unique = true)
public class PipelineStepTypeRecord {
    private Long id;
    @Column(length = 100) private String type;
    @Column(length = 100) private String name;
    @Column(length = 100) private String icon;
    @Column(length = 1000) private String description;
    @Column(length = 100) private String category;
    private Integer order;
    @Column(type = "text") private String params;
    @Column(length = 200) private String outputs;
    @Column(length = 1000) private String requires;

    public PipelineStepType toPojo(){
        PipelineStepType stepType = new PipelineStepType();

        stepType.setType(this.type);
        stepType.setName(this.name);
        stepType.setIcon(this.icon);
        stepType.setDescription(this.description);
        stepType.setCategory(this.category);
        stepType.setOrder(this.order);

        List<PipelineStepTypeParam> params = JSON.parseArray(this.params, PipelineStepTypeParam.class);
        stepType.setParams(params);

        List<String> outputs = JSON.parseArray(this.outputs, String.class);
        stepType.setOutputs(outputs);

        List<String> requires = JSON.parseArray(this.requires, String.class);
        stepType.setRequires(requires);

        return stepType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public String getRequires() {
        return requires;
    }

    public void setRequires(String requires) {
        this.requires = requires;
    }
}
