package com.airxiechao.y20.template.biz.process;

import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.api.ITemplateDb;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.Template;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TemplateBizProcess implements ITemplateBiz {

    private ITemplateDb templateDb = Db.get(ITemplateDb.class);

    @Override
    public TemplateRecord get(Long userId, Long templateId) {
        return templateDb.get(userId, templateId);
    }

    @Override
    public List<TemplateRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return templateDb.list(userId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long count(Long userId, String name) {
        return templateDb.count(userId, name);
    }

    @Override
    public TemplateRecord create(String name, String description, Long userId, String username, List<PipelineStep> steps, Map<String, PipelineVariable> variables) {
        Date now = new Date();

        Template template = new Template();
        template.setName(name);
        template.setDescription(description);
        template.setUserId(userId);
        template.setUsername(username);
        template.setSteps(steps);
        template.setVariables(variables);
        template.setCreateTime(now);
        template.setLastUpdateTime(now);

        TemplateRecord record = template.toRecord();
        boolean created = templateDb.insert(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public boolean update(TemplateRecord templateRecord) {
        templateRecord.setLastUpdateTime(new Date());
        return templateDb.update(templateRecord);
    }

    @Override
    public boolean delete(Long templateId) {
        return templateDb.delete(templateId);
    }
}
