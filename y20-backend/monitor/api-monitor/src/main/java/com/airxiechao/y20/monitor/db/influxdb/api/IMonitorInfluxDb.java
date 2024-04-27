package com.airxiechao.y20.monitor.db.influxdb.api;

import com.airxiechao.axcboot.core.annotation.IInfluxDb;
import com.airxiechao.axcboot.storage.db.influxdb.InfluxDbRecord;
import com.airxiechao.y20.monitor.db.influxdb.record.AgentMetricRecord;
import com.airxiechao.y20.monitor.db.influxdb.record.MonitorMetricRecord;

import java.util.List;
import java.util.Map;

@IInfluxDb("influxdb-y20-monitor.yml")
public interface IMonitorInfluxDb {
    boolean createMonitorMetric(MonitorMetricRecord metric);
    boolean createAgentMetric(AgentMetricRecord metric);

    Map<String, List<InfluxDbRecord>> listMonitorMetric(Long userId, Long projectId, Long monitorId, int rangeDays, int aggregateMinutes);
    Map<String, List<InfluxDbRecord>> listAgentMetric(Long userId, String agentId, int rangeDays, int aggregateMinutes);
}
