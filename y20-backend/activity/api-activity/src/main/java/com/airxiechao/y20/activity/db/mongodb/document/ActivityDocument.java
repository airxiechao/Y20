package com.airxiechao.y20.activity.db.mongodb.document;

import com.airxiechao.axcboot.storage.db.mongodb.annotation.MongoDbCollection;

import java.util.Date;

@MongoDbCollection("activity")
public class ActivityDocument {
    private Long userId;
    private String type;
    private String event;
    private Date time;

    public ActivityDocument() {
    }

    public ActivityDocument(Long userId, String type, String event, Date time) {
        this.userId = userId;
        this.type = type;
        this.event = event;
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
