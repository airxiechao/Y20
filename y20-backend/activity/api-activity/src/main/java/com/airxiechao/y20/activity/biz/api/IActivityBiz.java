package com.airxiechao.y20.activity.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.activity.db.mongodb.document.ActivityDocument;

import java.util.List;

@IBiz
public interface IActivityBiz {
    boolean create(Long userId, String type, String event);
    boolean createTemplateActivity(Long userId, Long templateId, String type, String param);
    List<ActivityDocument> list(Long userId, int pageNo, int pageSize);
}
