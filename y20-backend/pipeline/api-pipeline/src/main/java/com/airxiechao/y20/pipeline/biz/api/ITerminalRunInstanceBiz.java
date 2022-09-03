package com.airxiechao.y20.pipeline.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pipeline.run.terminal.ITerminalRunInstance;

@IBiz
public interface ITerminalRunInstanceBiz {
    ITerminalRunInstance createTerminalRunInstance(String pipelineRunInstanceUuid) throws Exception;
    void destroyTerminalRunInstance(String pipelineRunInstanceUuid, String terminalRunInstanceUuid) throws Exception;
    void inputTerminalRunInstance(String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String message) throws Exception;
}
