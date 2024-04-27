package com.airxiechao.y20.agent.client.scheduler;

import com.airxiechao.axcboot.task.quartz.annotation.Quartz;

@Quartz("quartz-y20-monitor.properties")
public interface IMonitorScheduler {
    void schedule();
    void shutdown();
}
