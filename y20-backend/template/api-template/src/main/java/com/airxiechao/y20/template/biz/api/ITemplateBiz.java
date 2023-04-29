package com.airxiechao.y20.template.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.template.db.record.TemplateRecord;

import java.util.List;
import java.util.Map;

@IBiz
public interface ITemplateBiz {

    TemplateRecord get(Long userId, Long templateId);

    List<TemplateRecord> list(
            Long userId,
            String name,
            String orderField,
            String orderType,
            Integer pageNo,
            Integer pageSize
    );
    long count(Long userId, String name);

    TemplateRecord create(
            String name, String description, Long userId, String username,
            List<PipelineStep> steps, Map<String, PipelineVariable> variables
    );

    boolean update(TemplateRecord templateRecord);
    boolean updateNumApply(TemplateRecord templateRecord);

    boolean delete(Long templateId);
}