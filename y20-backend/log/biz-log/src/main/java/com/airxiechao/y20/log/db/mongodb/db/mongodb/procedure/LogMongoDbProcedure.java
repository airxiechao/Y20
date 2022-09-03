package com.airxiechao.y20.log.db.mongodb.db.mongodb.procedure;

import com.airxiechao.axcboot.core.db.mongo.AbstractMongoDbProcedure;
import com.airxiechao.axcboot.storage.db.mongodb.MongoDbManager;
import com.airxiechao.y20.log.db.mongodb.api.ILogMongoDb;
import com.airxiechao.y20.log.db.mongodb.document.LogDocument;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.InsertOneResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class LogMongoDbProcedure extends AbstractMongoDbProcedure implements ILogMongoDb {

    private static final Logger logger = LoggerFactory.getLogger(LogMongoDbProcedure.class);

    public LogMongoDbProcedure(MongoDbManager dbManager) {
        super(dbManager);
    }

    @Override
    public boolean insertOne(LogDocument logDocument) {
        try{
            InsertOneResult res = this.dbManager.getCollection(LogDocument.class).insertOne(logDocument);
            return res.wasAcknowledged();
        }catch (Exception e){
            logger.error("insert log error", e);
            return false;
        }
    }

    @Override
    public String getLog(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Integer limit) {
        Deque<String> logs = new LinkedList<>();
        FindIterable<LogDocument> iter = this.dbManager.getCollection(LogDocument.class).find(
                and(eq("pipelineRunInstanceUuid", pipelineRunInstanceUuid),
                        eq("stepRunInstanceUuid", stepRunInstanceUuid)))
                .sort(descending("_id"));

        if(null != limit){
            iter = iter.limit(limit);
        }

        iter.forEach( log -> {
            logs.addFirst(log.getLog());
        });

        StringBuilder sb = new StringBuilder();
        logs.stream().forEach(log -> {
            sb.append(log);
        });
        return sb.toString();
    }
}
