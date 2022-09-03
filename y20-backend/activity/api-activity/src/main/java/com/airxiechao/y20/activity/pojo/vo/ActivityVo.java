package com.airxiechao.y20.activity.pojo.vo;

import com.airxiechao.y20.activity.db.mongodb.document.ActivityDocument;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;

public class ActivityVo {
    private Long userId;
    private String type;
    private JSONObject event;
    private Date time;

    public ActivityVo() {
    }

    public ActivityVo(ActivityDocument activityDocument) {
        this.userId = activityDocument.getUserId();
        this.type = activityDocument.getType();
        this.event = JSON.parseObject(activityDocument.getEvent());
        this.time = activityDocument.getTime();
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

    public JSONObject getEvent() {
        return event;
    }

    public void setEvent(JSONObject event) {
        this.event = event;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
