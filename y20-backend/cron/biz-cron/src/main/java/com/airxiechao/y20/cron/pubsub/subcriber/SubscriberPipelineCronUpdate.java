package com.airxiechao.y20.cron.pubsub.subcriber;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.cron.pojo.PipelineCronTask;
import com.airxiechao.y20.cron.schedular.IPipelineScheduler;
import com.airxiechao.y20.cron.scheduler.PipelineScheduler;
import com.airxiechao.y20.pipeline.pubsub.event.cron.EventPipelineCronUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SubscriberPipelineCronUpdate implements ISubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberPipelineCronUpdate.class);

    private static final IPipelineScheduler pipelineScheduler = PipelineScheduler.getInstance();

    @Override
    public Response handle(Map<String, Object> params) throws Exception {
        EventPipelineCronUpdate event = ModelUtil.fromMap(params, EventPipelineCronUpdate.class);
        Long pipelineId = event.getPipelineId();
        boolean flagCronEnabled = event.getFlagEnabled();
        boolean exists = pipelineScheduler.existsTask(pipelineId);

        logger.info("pipeline [{}] cron update", pipelineId);

        if(exists){
            pipelineScheduler.cancelTask(pipelineId);
        }

        if(flagCronEnabled){
            pipelineScheduler.scheduleTask(new PipelineCronTask(
                    pipelineId,
                    event.getBeginTime(),
                    event.getIntervalSecs(),
                    event.getInParams()
            ));
        }

        return new Response();
    }
}
