package com.airxiechao.y20.activity.pubsub.sink;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.activity.biz.api.IActivityBiz;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventSinkName;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.EventPipelineRunActivity;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Sink(
        events = { EnumPipelineEventType.PIPELINE_RUN_ACTIVITY +".*" },
        name = EnumPipelineEventSinkName.PIPELINE_RUN_ACTIVITY_SINK)
public class SinkPipelineRunActivity implements ISubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SinkPipelineRunActivity.class);
    private IActivityBiz activityBiz = Biz.get(IActivityBiz.class);

    @Override
    public Response handle(Map<String, Object> params) throws Exception {
        EventPipelineRunActivity event = ModelUtil.fromMap(params, EventPipelineRunActivity.class);
        Long userId = event.getUserId();
        Long pipelineRunId = event.getPipelineRunId();
        String status = event.getStatus();

        logger.info("pipeline run [{}] activity [{}]", pipelineRunId, status);

        switch (status){
            case EnumPipelineRunStatus.STATUS_PASSED:
            case EnumPipelineRunStatus.STATUS_FAILED:
                activityBiz.create(userId, EnumPipelineEventType.PIPELINE_RUN_ACTIVITY, JSON.toJSONString(event));
                break;
        }

        return new Response();
    }
}
