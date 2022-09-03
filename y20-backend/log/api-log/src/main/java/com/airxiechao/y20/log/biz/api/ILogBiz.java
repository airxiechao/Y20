package com.airxiechao.y20.log.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;

@IBiz
public interface ILogBiz {
    boolean append(String pipelineRunInstanceUuid, String stepRunInstanceUuid, String log);
    String getLog(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Integer limit);
}
