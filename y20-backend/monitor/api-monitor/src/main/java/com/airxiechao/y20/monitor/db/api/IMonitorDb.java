package com.airxiechao.y20.monitor.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;

import java.util.List;
import java.util.Map;

@IDb("mybatis-y20-monitor.xml")
public interface IMonitorDb {

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

    boolean create(MonitorRecord record);

    boolean update(MonitorRecord record);

    boolean deleteById(Long userId, Long projectId, Long monitorId);
}
