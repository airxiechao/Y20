package com.airxiechao.y20.log.db.mongodb.api;

import com.airxiechao.axcboot.core.annotation.IMongoDb;
import com.airxiechao.y20.log.db.mongodb.document.LogDocument;

@IMongoDb("mongodb-y20-log.yml")
public interface ILogMongoDb {
    boolean insertOne(LogDocument logDocument);
    String getLog(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Integer limit);
}
