package com.airxiechao.y20.template.db.record;

import com.airxiechao.axcboot.storage.annotation.Column;
import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.template.pojo.Template;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Table("template")
@Index(fields = {"name", "description"}, fulltext = true)
public class TemplateRecord {
    private Long id;
    private Long userId;
    private String username;
    @Column(length = 100) private String name;
    @Column(type = "text") private String description;
    @Column(type = "text") private String steps;
    @Column(type = "text") private String variables;
    private Date createTime;
    private Date lastUpdateTime;

    public Template toPojo(){
        Template template = new Template();
        template.setTemplateId(id);
        template.setName(name);
        template.setDescription(description);
        template.setUserId(userId);
        template.setUsername(username);

        List<PipelineStep> steps = JSON.parseArray(this.steps, PipelineStep.class);
        template.setSteps(steps);

        Map<String, PipelineVariable> variables = JSON.parseObject(this.variables,
                new TypeReference<LinkedHashMap<String, PipelineVariable>>(){});
        template.setVariables(variables);

        template.setCreateTime(createTime);
        template.setLastUpdateTime(lastUpdateTime);

        return template;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
