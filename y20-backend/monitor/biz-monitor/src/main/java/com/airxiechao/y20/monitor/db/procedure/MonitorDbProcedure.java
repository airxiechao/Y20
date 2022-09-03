package com.airxiechao.y20.monitor.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.monitor.db.api.IMonitorDb;
import com.airxiechao.y20.monitor.db.record.MonitorRecord;

import java.util.List;

public class MonitorDbProcedure extends AbstractDbProcedure implements IMonitorDb {

    public MonitorDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public MonitorRecord getById(Long userId, Long projectId, Long monitorId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(MonitorRecord.class))
                .where(DbUtil.column(MonitorRecord.class, "id"), "=", monitorId)
                .where(DbUtil.column(MonitorRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(MonitorRecord.class, "projectId"), "=", projectId)
                .build();

        return this.dbManager.selectFirstBySql(sqlParams, MonitorRecord.class);
    }

    @Override
    public List<MonitorRecord> list(Long userId, Long projectId, String agentId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(MonitorRecord.class))
                .where(DbUtil.column(MonitorRecord.class, "userId"), "=", userId);

        if(null != projectId){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "projectId"), "=", projectId);
        }

        if(!StringUtil.isBlank(agentId)){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "agentId"), "=", agentId);
        }

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)) {
            sqlParamsBuilder.order(DbUtil.column(MonitorRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, MonitorRecord.class);
    }

    @Override
    public long count(Long userId, Long projectId, String agentId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(MonitorRecord.class))
                .where(DbUtil.column(MonitorRecord.class, "userId"), "=", userId);

        if(null != projectId){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "projectId"), "=", projectId);
        }

        if(!StringUtil.isBlank(agentId)){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "agentId"), "=", agentId);
        }

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(MonitorRecord.class, "name"), "like", "%" + name + "%");
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, MonitorRecord.class);
    }

    @Override
    public boolean create(MonitorRecord record) {
        return dbManager.insert(record) > 0;
    }

    @Override
    public boolean update(MonitorRecord record) {
        return dbManager.update(record) > 0;
    }

    @Override
    public boolean deleteById(Long userId, Long projectId, Long monitorId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(MonitorRecord.class))
                .where(DbUtil.column(MonitorRecord.class, "id"), "=", monitorId)
                .where(DbUtil.column(MonitorRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(MonitorRecord.class, "projectId"), "=", projectId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, MonitorRecord.class) > 0;
        return deleted;
    }
}
