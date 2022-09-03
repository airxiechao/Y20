package com.airxiechao.y20.cron;

import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.cron.pubsub.subcriber.SubscriberPipelineCronUpdate;
import com.airxiechao.y20.cron.rest.server.CronRestServer;
import com.airxiechao.y20.cron.schedular.IPipelineScheduler;
import com.airxiechao.y20.cron.scheduler.PipelineScheduler;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineEventType;
import com.airxiechao.y20.pipeline.pubsub.event.cron.EventPipelineCronUpdate;

public class CronBoot {
    public static void main(String[] args){

        // rest server
        CronRestServer cronRestServer = CronRestServer.getInstance();

        // register event subscriber
        EventBus.getInstance().getPubSub().subscribe(
                EventPipelineCronUpdate.type(null),
                cronRestServer.getUuid(),
                new SubscriberPipelineCronUpdate()
        );

        // scheduler sync
        IPipelineScheduler pipelineScheduler = PipelineScheduler.getInstance();
        pipelineScheduler.syncTask();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cronRestServer.stop();
            pipelineScheduler.shutdown();
        }));

        cronRestServer.start();
    }
}
