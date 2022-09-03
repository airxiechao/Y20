package com.airxiechao.y20.monitor.scheduler;

import com.airxiechao.axcboot.task.ITaskScheduler;
import com.airxiechao.axcboot.task.TaskSchedulerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MonitorScheduler implements IMonitorScheduler {

    private static final int INTERVAL_SECS = 30;

    private static final Logger logger = LoggerFactory.getLogger(MonitorScheduler.class);

    private static final MonitorScheduler instance = new MonitorScheduler();
    public static MonitorScheduler getInstance(){
        return instance;
    }
    private MonitorScheduler(){}

    private ITaskScheduler taskScheduler = TaskSchedulerManager.getInstance().getQuartzScheduler();

    @Override
    public void schedule() {
        logger.info("schedule monitor task every [{}] secs", INTERVAL_SECS);

        ScheduledFuture future = taskScheduler.schedulePeriodAfter(
                INTERVAL_SECS, INTERVAL_SECS, TimeUnit.SECONDS, new MonitorJob());
    }

    @Override
    public void shutdown() {
        taskScheduler.shutdownNow();
    }
}
