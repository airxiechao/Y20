package com.airxiechao.y20.pipeline.biz.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;

import java.util.List;

@IBiz
public interface IPipelineRunInstanceBiz {
    List<IPipelineRunInstance> listRunInstance();
    IPipelineRunInstance createRunInstance(PipelineRunDetailVo pipelineRunDetail);
    IPipelineRunInstance getRunInstance(String pipelineRunInstanceUuid);
    boolean stepRunInstanceCallback(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Response response);
}
