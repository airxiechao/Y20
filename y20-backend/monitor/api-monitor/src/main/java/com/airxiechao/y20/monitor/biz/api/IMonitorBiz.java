package com.airxiechao.y20.monitor.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;
import com.airxiechao.y20.monitor.pojo.AgentMetric;
import com.airxiechao.y20.monitor.pojo.MetricPoint;
import com.airxiechao.y20.monitor.pojo.MonitorMetric;

import java.util.List;
import java.util.Map;

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

    MonitorRecord create(Long userId, Long projectId, String agentId, String name, String type, Object target, String actionType, Object actionParam);

    boolean updateBasic(Long userId, Long projectId, Long monitorId, String agentId, String name, String type, Object target, String actionType, Object actionParam);
    boolean updateStatus(Long userId, Long projectId, Long monitorId, String status);

    boolean deleteById(Long userId, Long projectId, Long monitorId);

    boolean createMonitorMetric(MonitorMetric metric);
    boolean createAgentMetric(AgentMetric metric);

    Map<String, List<MetricPoint>> listMonitorMetric(Long userId, Long projectId, Long monitorId, int rangeDays, int aggregateMinutes);
    Map<String, List<MetricPoint>> listAgentMetric(Long userId, String agentId, int rangeDays, int aggregateMinutes);

}
