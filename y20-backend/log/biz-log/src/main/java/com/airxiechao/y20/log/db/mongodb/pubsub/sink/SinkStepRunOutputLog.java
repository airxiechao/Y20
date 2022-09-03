package com.airxiechao.y20.log.db.mongodb.pubsub.sink;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.log.biz.api.ILogBiz;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventSinkName;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pubsub.event.step.EventStepRunOutputPush;

import java.util.Map;

@Sink(
        events = { EnumPipelineEventType.STEP_RUN_OUTPUT_PUSH +".*.*" },
        name = EnumPipelineEventSinkName.STEP_RUN_OUTPUT_LOG_SINK)
public class SinkStepRunOutputLog implements ISubscriber {

    private ILogBiz logBiz = Biz.get(ILogBiz.class);

    @Override
    public Response handle(Map<String, Object> map) throws Exception {

        EventStepRunOutputPush event = ModelUtil.fromMap(map, EventStepRunOutputPush.class);

        String output = event.getOutput();
        if(output.equals("\0") || StringUtil.isEmpty(output)){
            return new Response();
        }

        boolean appended = logBiz.append(event.getPipelineRunInstanceUuid(), event.getStepRunInstanceUuid(), output);
        if(!appended){
            return new Response().error();
        }

        return new Response();
    }
}