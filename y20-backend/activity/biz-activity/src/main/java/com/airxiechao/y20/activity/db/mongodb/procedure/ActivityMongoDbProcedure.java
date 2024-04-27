package com.airxiechao.y20.activity.db.mongodb.procedure;

import com.airxiechao.axcboot.core.db.mongo.AbstractMongoDbProcedure;
import com.airxiechao.axcboot.storage.db.mongodb.MongoDbManager;
import com.airxiechao.y20.activity.db.mongodb.api.IActivityMongoDb;
import com.airxiechao.y20.activity.db.mongodb.document.ActivityDocument;
import com.airxiechao.y20.activity.db.mongodb.document.TemplateActivityDocument;
import com.mongodb.client.result.InsertOneResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class ActivityMongoDbProcedure extends AbstractMongoDbProcedure implements IActivityMongoDb {

    private static final Logger logger = LoggerFactory.getLogger(ActivityMongoDbProcedure.class);

    public ActivityMongoDbProcedure(MongoDbManager dbManager) {
        super(dbManager);
    }

    @Override
    public boolean insert(ActivityDocument activityDocument) {
        try{
            InsertOneResult res = this.dbManager.getCollection(ActivityDocument.class).insertOne(activityDocument);
            return res.wasAcknowledged();
        }catch (Exception e){
            logger.error("insert activity error", e);
            return false;
        }
    }

    @Override
    public boolean insertTemplateActivity(TemplateActivityDocument templateActivityDocument) {
        try{
            InsertOneResult res = this.dbManager.getCollection(TemplateActivityDocument.class).insertOne(templateActivityDocument);
            return res.wasAcknowledged();
        }catch (Exception e) {
            logger.error("insert template activity error", e);
            return false;
        }
    }

    @Override
    public List<ActivityDocument> list(Long userId, int pageNo, int pageSize) {
        List<ActivityDocument> list = new ArrayList<>();
        this.dbManager.getCollection(ActivityDocument.class).find(
                and(eq("userId", userId)))
                .sort(descending("time"))
                .skip((pageNo-1) * pageSize)
                .limit(pageSize)
                .forEach( log -> {
                    list.add(log);
                });
        return list;
    }
}
