package com.airxiechao.y20.template.biz.process;

import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.api.ITemplateDb;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.Template;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateActionType;
import com.airxiechao.y20.template.pojo.pubsub.event.EventTemplateActivity;
import com.airxiechao.y20.template.search.TemplateIndex;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateBizProcess implements ITemplateBiz {

    private ITemplateDb templateDb = Db.get(ITemplateDb.class);

    @Override
    public TemplateRecord get(Long userId, Long templateId) {
        TemplateRecord record = templateDb.get(userId, templateId);
        return record;
    }

    @Override
    public List<TemplateRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        List<TemplateRecord> records;
        if(StringUtil.isBlank(name) || name.startsWith("@")){
            records = templateDb.list(userId, name, orderField, orderType, pageNo, pageSize);
        }else{
            List<String> list = TemplateIndex.getInstance().query(userId, name, pageNo, pageSize);
            records = list.stream().map(templateId -> get(userId, Long.valueOf(templateId))).collect(Collectors.toList());
        }

        return records;
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

        EventBus.getInstance().publish(
                new EventTemplateActivity(record.getUserId(), record.getId(), EnumTemplateActionType.ADD, null));

        return record;
    }

    @Override
    public boolean update(TemplateRecord templateRecord) {
        templateRecord.setLastUpdateTime(new Date());
        boolean updated = templateDb.update(templateRecord);

        EventBus.getInstance().publish(
                new EventTemplateActivity(templateRecord.getUserId(), templateRecord.getId(), EnumTemplateActionType.UPDATE, null));

        return updated;
    }

    @Override
    public boolean updateNumApply(TemplateRecord templateRecord) {
        return templateDb.updateNumApply(templateRecord);
    }

    @Override
    public boolean delete(Long templateId) {
        boolean deleted = templateDb.delete(templateId);

        EventBus.getInstance().publish(
                new EventTemplateActivity(null, templateId, EnumTemplateActionType.DELETE, null));

        return deleted;
    }
}
