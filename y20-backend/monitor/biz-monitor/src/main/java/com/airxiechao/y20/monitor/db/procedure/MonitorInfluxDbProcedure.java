package com.airxiechao.y20.monitor.db.procedure;

import com.airxiechao.axcboot.core.db.influxdb.AbstractInfluxDbProcedure;
import com.airxiechao.axcboot.storage.db.influxdb.InfluxDbManager;
import com.airxiechao.axcboot.storage.db.influxdb.InfluxDbRecord;
import com.airxiechao.axcboot.storage.db.influxdb.annotation.InfluxDbMeasurement;
import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.os.FileSystemMetric;
import com.airxiechao.y20.monitor.db.influxdb.api.IMonitorInfluxDb;
import com.airxiechao.y20.monitor.db.influxdb.record.AgentMetricRecord;
import com.airxiechao.y20.monitor.db.influxdb.record.MonitorMetricRecord;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MonitorInfluxDbProcedure extends AbstractInfluxDbProcedure implements IMonitorInfluxDb {

    private static final Logger logger = LoggerFactory.getLogger(MonitorInfluxDbProcedure.class);

    public MonitorInfluxDbProcedure(InfluxDbManager dbManager) {
        super(dbManager);
    }

    @Override
    public boolean createMonitorMetric(MonitorMetricRecord metric) {
        try {
            dbManager.write(metric);
            return true;
        } catch (Exception e) {
            logger.error("create monitor metric error", e);
            return false;
        }
    }

    @Override
    public boolean createAgentMetric(AgentMetricRecord metric) {
        try {
            dbManager.write(metric);
            return true;
        } catch (Exception e) {
            logger.error("create agent metric error", e);
            return false;
        }
    }

    @Override
    public Map<String, List<InfluxDbRecord>> listMonitorMetric(Long userId, Long projectId, Long monitorId, int rangeDays, int aggregateMinutes) {
        InfluxDbMeasurement annotation = AnnotationUtil.getClassAnnotation(MonitorMetricRecord.class, InfluxDbMeasurement.class);
        String measurement = annotation.value();

        Map<String, List<InfluxDbRecord>> result = dbManager.query(Arrays.asList(
                String.format("range(start: -%dd)", rangeDays),
                String.format("filter(fn: (r) => r[\"_measurement\"] == \"%s\")", measurement),
                String.format("filter(fn: (r) => r[\"userId\"] == \"%d\")", userId),
                String.format("filter(fn: (r) => r[\"projectId\"] == \"%d\")", projectId),
                String.format("filter(fn: (r) => r[\"monitorId\"] == \"%d\")", monitorId),
                String.format("aggregateWindow(every: %dm, fn: last, createEmpty: true)", aggregateMinutes),
                String.format("yield()")
        ));

        return result;
    }

    @Override
    public Map<String, List<InfluxDbRecord>> listAgentMetric(Long userId, String agentId, int rangeDays, int aggregateMinutes) {
        InfluxDbMeasurement annotation = AnnotationUtil.getClassAnnotation(AgentMetricRecord.class, InfluxDbMeasurement.class);
        String measurement = annotation.value();

        Map<String, List<InfluxDbRecord>> result = dbManager.query(Arrays.asList(
                String.format("range(start: -%dd)", rangeDays),
                String.format("filter(fn: (r) => r[\"_measurement\"] == \"%s\")", measurement),
                String.format("filter(fn: (r) => r[\"userId\"] == \"%d\")", userId),
                String.format("filter(fn: (r) => r[\"agentId\"] == \"%s\")", agentId),
                String.format("aggregateWindow(every: %dm, fn: last, createEmpty: true)", aggregateMinutes),
                String.format("yield()")
        ));

        return result;
    }
}
