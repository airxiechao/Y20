package com.airxiechao.y20.cron.scheduler;

import com.airxiechao.axcboot.task.ITaskScheduler;
import com.airxiechao.axcboot.task.TaskSchedulerManager;
import com.airxiechao.axcboot.util.TimeUtil;
import com.airxiechao.y20.cron.biz.process.PipelineCronTaskPageIterator;
import com.airxiechao.y20.cron.pojo.PipelineCronTask;
import com.airxiechao.y20.cron.schedular.IPipelineScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PipelineScheduler implements IPipelineScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PipelineScheduler.class);

    private static final PipelineScheduler instance = new PipelineScheduler();
    public static PipelineScheduler getInstance(){
        return instance;
    }

    private PipelineScheduler(){}

    private ITaskScheduler taskScheduler = TaskSchedulerManager.getInstance().getQuartzScheduler();
    private Map<Long, ScheduledFuture> taskFutures = new ConcurrentHashMap<>();

    @Override
    public void syncTask() {
        logger.info("sync pipeline cron task");

        int total = 0;
        PipelineCronTaskPageIterator it = new PipelineCronTaskPageIterator(5000);
        while (it.hasNext()){
            List<PipelineCronTask> page = it.next();
            total += page.size();
            page.forEach(this::scheduleTask);
        }

        logger.info("sync pipeline cron task -> total [{}]", total);
    }

    @Override
    public boolean scheduleTask(PipelineCronTask task) {
        Long pipelineId = task.getPipelineId();
        int intervalSecs = task.getIntervalSecs();
        Date beginTime = task.getBeginTime();
        Map<String, String> inParams = task.getInParams();

        if(existsTask(pipelineId)){
            logger.info("pipeline [{}] cron task already exists", pipelineId);
            return false;
        }

        int delay;
        try {
            delay = calDelaySecs(beginTime, intervalSecs);
        } catch (Exception e) {
            logger.info("calculate delay secs error", e.getMessage());
            return false;
        }

        logger.info("schedule pipeline [{}] cron task, begin at [{}] every [{}] secs",
                pipelineId, TimeUtil.toTimeStr(beginTime), intervalSecs);

        ScheduledFuture future = taskScheduler.schedulePeriodAfter(
                delay, intervalSecs, TimeUnit.SECONDS,
                new PipelineCronJob(pipelineId, intervalSecs, inParams));
        taskFutures.put(task.getPipelineId(), future);

        return true;
    }

    @Override
    public boolean cancelTask(Long pipelineId){
        ScheduledFuture future = taskFutures.get(pipelineId);
        if(null == future){
            logger.info("pipeline [{}] cron task not exists", pipelineId);
            return false;
        }

        boolean canceled = future.cancel(true);
        if(canceled){
            taskFutures.remove(pipelineId);
        }
        logger.info("cancel pipeline [{}] cron task -> [{}]", pipelineId, canceled);
        return canceled;
    }

    @Override
    public boolean existsTask(Long pipelineId){
        return taskFutures.containsKey(pipelineId);
    }

    @Override
    public void shutdown() {
        taskScheduler.shutdownNow();
    }

    private int calDelaySecs(Date beginTime, int intervalSecs) throws Exception {
        if(intervalSecs <= 0){
            throw new Exception("pipeline cron task interval secs <= 0");
        }

        long now = System.currentTimeMillis() / 1000;
        long begin = beginTime.getTime() / 1000;

        while(begin < now){
            begin += intervalSecs;
        }

        return (int)(begin - now);
    }
}
