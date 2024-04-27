package com.airxiechao.y20.activity.biz.process;

import com.airxiechao.y20.activity.biz.api.IActivityBiz;
import com.airxiechao.y20.activity.db.mongodb.api.IActivityMongoDb;
import com.airxiechao.y20.activity.db.mongodb.document.ActivityDocument;
import com.airxiechao.y20.activity.db.mongodb.document.TemplateActivityDocument;
import com.airxiechao.y20.common.core.db.MongoDb;

import java.util.Date;
import java.util.List;

public class ActivityBizProcess implements IActivityBiz {

    private IActivityMongoDb activityMongoDb = MongoDb.get(IActivityMongoDb.class);

    @Override
    public boolean create(Long userId, String type, String event) {
        ActivityDocument activityDocument = new ActivityDocument(userId, type, event, new Date());
        boolean created = activityMongoDb.insert(activityDocument);
        return created;
    }

    @Override
    public boolean createTemplateActivity(Long userId, Long templateId, String type, String param) {
        TemplateActivityDocument templateActivityDocument = new TemplateActivityDocument(userId, templateId, type, param, new Date());
        boolean created = activityMongoDb.insertTemplateActivity(templateActivityDocument);
        return created;
    }

    @Override
    public List<ActivityDocument> list(Long userId, int pageNo, int pageSize) {
        List<ActivityDocument> list = activityMongoDb.list(userId, pageNo, pageSize);
        return list;
    }
}
