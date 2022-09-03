package com.airxiechao.y20.pipeline.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

import java.util.Date;

public class CountPipelineRunParam {
    @Required private Long userId;
    @Required private Date beginTime;
    @Required private Date endTime;

    public CountPipelineRunParam() {
    }

    public CountPipelineRunParam(Long userId, Date beginTime, Date endTime) {
        this.userId = userId;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
