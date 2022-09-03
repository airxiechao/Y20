package com.airxiechao.y20.log.db.mongodb.biz.process;

import com.airxiechao.y20.common.core.db.MongoDb;
import com.airxiechao.y20.log.biz.api.ILogBiz;
import com.airxiechao.y20.log.db.mongodb.api.ILogMongoDb;
import com.airxiechao.y20.log.db.mongodb.document.LogDocument;

import java.util.Date;

public class LogBizProcess implements ILogBiz {

    private ILogMongoDb logMongoDb = MongoDb.get(ILogMongoDb.class);

    @Override
    public boolean append(String pipelineRunInstanceUuid, String stepRunInstanceUuid, String log) {
        boolean inserted = logMongoDb.insertOne(new LogDocument(pipelineRunInstanceUuid, stepRunInstanceUuid, log, new Date()));
        return inserted;
    }

    @Override
    public String getLog(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Integer limit) {
        return logMongoDb.getLog(pipelineRunInstanceUuid, stepRunInstanceUuid, limit);
    }
}
