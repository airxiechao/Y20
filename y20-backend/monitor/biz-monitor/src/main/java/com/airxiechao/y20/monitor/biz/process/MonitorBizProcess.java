package com.airxiechao.y20.monitor.biz.process;

import com.airxiechao.axcboot.storage.db.influxdb.InfluxDbRecord;
import com.airxiechao.axcboot.util.os.FileSystemMetric;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.common.core.db.InfluxDb;
import com.airxiechao.y20.monitor.biz.api.IMonitorBiz;
import com.airxiechao.y20.monitor.db.api.IMonitorDb;
import com.airxiechao.y20.monitor.db.influxdb.api.IMonitorInfluxDb;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;
import com.airxiechao.y20.monitor.pojo.AgentMetric;
import com.airxiechao.y20.monitor.pojo.MetricPoint;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.airxiechao.y20.monitor.pojo.MonitorMetric;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorStatus;
import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.stream.Collectors;

public class MonitorBizProcess implements IMonitorBiz {

    private IMonitorDb monitorDb = Db.get(IMonitorDb.class);
    private IMonitorInfluxDb monitorInfluxDb = InfluxDb.get(IMonitorInfluxDb.class);

    @Override
    public MonitorRecord getById(Long userId, Long projectId, Long monitorId) {
        return monitorDb.getById(userId, projectId, monitorId);
    }

    @Override
    public List<MonitorRecord> list(Long userId, Long projectId, String agentId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return monitorDb.list(userId, projectId, agentId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long count(Long userId, Long projectId, String agentId, String name) {
        return monitorDb.count(userId, projectId, agentId, name);
    }

    @Override
    public MonitorRecord create(Long userId, Long projectId, String agentId, String name, String type, Object target, String actionType, Object actionParam) {
        Monitor monitor = new Monitor();
        monitor.setUserId(userId);
        monitor.setProjectId(projectId);
        monitor.setAgentId(agentId);
        monitor.setName(name);
        monitor.setType(type);
        monitor.setTarget(target);
        monitor.setActionType(actionType);
        monitor.setActionParam(actionParam);
        monitor.setStatus(EnumMonitorStatus.UNKNOWN);
        monitor.setCreateTime(new Date());

        MonitorRecord record = monitor.toRecord();
        boolean created = monitorDb.create(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public boolean updateBasic(Long userId, Long projectId, Long monitorId, String agentId, String name, String type, Object target, String actionType, Object actionParam) {
        MonitorRecord monitorRecord = monitorDb.getById(userId, projectId, monitorId);

        Monitor monitor = monitorRecord.toPojo();
        monitor.setAgentId(agentId);
        monitor.setName(name);
        monitor.setType(type);
        monitor.setTarget(target);
        monitor.setActionType(actionType);
        monitor.setActionParam(actionParam);

        return monitorDb.update(monitor.toRecord());
    }

    @Override
    public boolean updateStatus(Long userId, Long projectId, Long monitorId, String status) {
        MonitorRecord monitorRecord = monitorDb.getById(userId, projectId, monitorId);

        Monitor monitor = monitorRecord.toPojo();
        monitor.setStatus(status);
        monitor.setLastUpdateTime(new Date());

        return monitorDb.update(monitor.toRecord());
    }

    @Override
    public boolean deleteById(Long userId, Long projectId, Long monitorId) {
        return monitorDb.deleteById(userId, projectId, monitorId);
    }

    @Override
    public boolean createMonitorMetric(MonitorMetric metric) {
        return monitorInfluxDb.createMonitorMetric(metric.toRecord());
    }

    @Override
    public boolean createAgentMetric(AgentMetric metric) {
        return monitorInfluxDb.createAgentMetric(metric.toRecord());
    }

    @Override
    public Map<String, List<MetricPoint>> listMonitorMetric(Long userId, Long projectId, Long monitorId, int rangeDays, int aggregateMinutes) {
        Map<String, List<InfluxDbRecord>> metrics = monitorInfluxDb.listMonitorMetric(userId, projectId, monitorId, rangeDays, aggregateMinutes);
        return convertMetrics(metrics);
    }

    @Override
    public Map<String, List<MetricPoint>> listAgentMetric(Long userId, String agentId, int rangeDays, int aggregateMinutes) {
        Map<String, List<InfluxDbRecord>> metrics = monitorInfluxDb.listAgentMetric(userId, agentId, rangeDays, aggregateMinutes);
        Map<String, List<MetricPoint>> result = convertMetrics(metrics);

        // parse filesystem metric
        List<MetricPoint> filesystem = result.get("filesystem");
        if(null != filesystem){
            // collect mount
            Set<String> mountSet = new LinkedHashSet<>();
            for (MetricPoint metric : filesystem) {
                Object value = metric.getValue();
                if(null != value && value instanceof String){
                    List<FileSystemMetric> fsList = JSON.parseArray(value.toString(), FileSystemMetric.class);
                    Map<String, FileSystemMetric> fsMap = new HashMap<>();
                    for (FileSystemMetric fs : fsList) {
                        fsMap.put(fs.getMount(), fs);
                    }
                    metric.setValue(fsMap);

                    for (FileSystemMetric fs : fsList) {
                        String mount = fs.getMount();
                        if(!mountSet.contains(mount)) {
                            mountSet.add(mount);
                        }
                    }
                }
            }

            // collect metric for every mount
            Map<String, List<MetricPoint>> filesystemMetricMap = new LinkedHashMap<>();
            for (String mount : mountSet) {
                filesystemMetricMap.put(mount, new ArrayList<>());
            }

            for (MetricPoint metric : filesystem) {
                Long timestamp = metric.getTimestamp();
                Object value = metric.getValue();

                if(null == value){
                    for (String mount : mountSet) {
                        filesystemMetricMap.get(mount).add(new MetricPoint(null, timestamp));
                    }
                }else{
                    Map<String, FileSystemMetric> fsMap = (Map<String, FileSystemMetric>)value;
                    for (String mount : mountSet) {
                        FileSystemMetric fs = fsMap.get(mount);
                        filesystemMetricMap.get(mount).add(new MetricPoint(fs, timestamp));
                    }
                }
            }

            result.remove("filesystem");

            for (Map.Entry<String, List<MetricPoint>> entry : filesystemMetricMap.entrySet()) {
                String mount = entry.getKey();
                List<MetricPoint> list = entry.getValue();

                result.put(String.format("filesystem:%s", mount), list);
            }
        }

        return result;
    }

    private Map<String, List<MetricPoint>> convertMetrics(Map<String, List<InfluxDbRecord>> metrics){
        Map<String, List<MetricPoint>> result = new HashMap<>();

        for (String key : metrics.keySet()) {
            result.put(key, metrics.get(key).stream().map(m -> new MetricPoint(m.getValue(), m.getTimestamp())).collect(Collectors.toList()));
        }

        return result;
    }
}
