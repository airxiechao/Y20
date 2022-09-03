package com.airxiechao.y20.cron.schedular;

import com.airxiechao.y20.cron.pojo.PipelineCronTask;

public interface IPipelineScheduler {
    void syncTask();
    boolean scheduleTask(PipelineCronTask task);
    boolean cancelTask(Long pipelineId);
    boolean existsTask(Long pipelineId);
    void shutdown();
}
