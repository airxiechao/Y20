package com.airxiechao.y20.monitor.biz.process;

import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.monitor.biz.api.IMonitorBiz;
import com.airxiechao.y20.monitor.db.api.IMonitorDb;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorStatus;

import java.util.Date;
import java.util.List;

public class MonitorBizProcess implements IMonitorBiz {

    private IMonitorDb monitorDb = Db.get(IMonitorDb.class);

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
}
