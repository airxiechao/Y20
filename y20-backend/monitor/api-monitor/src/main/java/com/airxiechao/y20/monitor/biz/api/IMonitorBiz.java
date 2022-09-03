package com.airxiechao.y20.monitor.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;

import java.util.List;

@IBiz
public interface IMonitorBiz {

    MonitorRecord getById(Long userId, Long projectId, Long monitorId);

    List<MonitorRecord> list(
            Long userId,
            Long projectId,
            String agentId,
            String name,
            String orderField,
            String orderType,
            Integer pageNo,
            Integer pageSize
    );

    long count(Long userId, Long projectId, String agentId, String name);

    MonitorRecord create(Long userId, Long projectId, String agentId, String name, String type, Object target);

    boolean updateBasic(Long userId, Long projectId, Long monitorId, String agentId, String name, String type, Object target);
    boolean updateStatus(Long userId, Long projectId, Long monitorId, String status);

    boolean deleteById(Long userId, Long projectId, Long monitorId);
}
