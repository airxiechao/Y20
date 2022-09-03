package com.airxiechao.y20.log.db.mongodb.document;

import com.airxiechao.axcboot.storage.db.mongodb.annotation.MongoDbCollection;

import java.util.Date;

@MongoDbCollection("log")
public class LogDocument {
    private String pipelineRunInstanceUuid;
    private String stepRunInstanceUuid;
    private String log;
    private Date time;

    public LogDocument() {
    }

    public LogDocument(String pipelineRunInstanceUuid, String stepRunInstanceUuid, String log, Date time) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.stepRunInstanceUuid = stepRunInstanceUuid;
        this.log = log;
        this.time = time;
    }

    public String getPipelineRunInstanceUuid() {
        return pipelineRunInstanceUuid;
    }

    public void setPipelineRunInstanceUuid(String pipelineRunInstanceUuid) {
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
    }

    public String getStepRunInstanceUuid() {
        return stepRunInstanceUuid;
    }

    public void setStepRunInstanceUuid(String stepRunInstanceUuid) {
        this.stepRunInstanceUuid = stepRunInstanceUuid;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
