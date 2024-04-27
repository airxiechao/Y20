package com.airxiechao.y20.pipeline.scheduler;

import com.airxiechao.axcboot.task.ITaskScheduler;
import com.airxiechao.axcboot.task.TaskSchedulerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PtyCleanScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PtyCleanScheduler.class);

    private static final PtyCleanScheduler instance = new PtyCleanScheduler();
    public static PtyCleanScheduler getInstance(){
        return instance;
    }

    private ITaskScheduler taskScheduler = TaskSchedulerManager.getInstance().getTaskScheduler(
            "pty-clean-scheduler", 1);;
    private Map<String, ScheduledFuture> futures = new ConcurrentHashMap<>();

    private PtyCleanScheduler(){}

    public void schedule(String ptyInstanceUuid, Runnable runnable){
        ScheduledFuture future = taskScheduler.scheduleOnceAfter(1, TimeUnit.DAYS, () -> {
            try{
                runnable.run();
            }catch (Exception e){
                logger.error("pty clean scheduler error", e);
            }finally {
                futures.remove(ptyInstanceUuid);
            }
        });
        futures.put(ptyInstanceUuid, future);

        logger.info("pty clean scheduler schedule: {}, pending size: {}", ptyInstanceUuid, futures.size());
    }

    public void cancel(String ptyOInstanceUuid){
        ScheduledFuture future = futures.get(ptyOInstanceUuid);
        if(null != future){
            future.cancel(true);
            futures.remove(ptyOInstanceUuid);
        }
    }

    public void shutdown() {
        taskScheduler.shutdownNow();
        futures.clear();
    }
}
