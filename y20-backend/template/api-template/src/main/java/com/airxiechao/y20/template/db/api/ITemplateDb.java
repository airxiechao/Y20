package com.airxiechao.y20.template.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.template.db.record.TemplateRecord;

import java.util.List;

@IDb("mybatis-y20-template.xml")
public interface ITemplateDb {
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
    boolean insert(TemplateRecord templateRecord);
    boolean update(TemplateRecord templateRecord);
    boolean delete(long templateId);
}
