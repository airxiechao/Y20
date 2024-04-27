package com.airxiechao.y20.activity.db.mongodb.api;

import com.airxiechao.axcboot.core.annotation.IMongoDb;
import com.airxiechao.y20.activity.db.mongodb.document.ActivityDocument;
import com.airxiechao.y20.activity.db.mongodb.document.TemplateActivityDocument;

import java.util.List;

@IMongoDb("mongodb-y20-activity.yml")
public interface IActivityMongoDb {
    boolean insert(ActivityDocument activityDocument);
    boolean insertTemplateActivity(TemplateActivityDocument templateActivityDocument);
    List<ActivityDocument> list(Long userId, int pageNo, int pageSize);
}
