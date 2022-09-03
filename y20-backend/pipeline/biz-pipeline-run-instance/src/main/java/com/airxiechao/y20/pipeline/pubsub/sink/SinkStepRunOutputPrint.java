package com.airxiechao.y20.pipeline.pubsub.sink;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventSinkName;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pubsub.event.step.EventStepRunOutputPush;

import java.util.Map;

/**
 * sink all step run output for debug
 */
//@Sink(
//        events = { EnumPipelineEventType.STEP_RUN_OUTPUT_PUSH +".*.*" },
//        name = EnumPipelineEventSinkName.STEP_RUN_OUTPUT_PRINT_SINK)
public class SinkStepRunOutputPrint implements ISubscriber {

    @Override
    public Response handle(Map<String, Object> map) throws Exception {

        EventStepRunOutputPush event = ModelUtil.fromMap(map, EventStepRunOutputPush.class);

        String log = event.getOutput();
        if(!log.equals("\0")){
            System.out.print(log);
        }

        return new Response();
    }
}
