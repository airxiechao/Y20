package com.airxiechao.y20.agent.db.procedure;

import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.db.api.IAgentDb;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;

import java.util.List;

public class AgentDbProcedure extends AbstractDbProcedure implements IAgentDb {

    public AgentDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public AgentRecord getByUserIdAndAgentId(Long userId, String agentId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentRecord.class))
                .where(DbUtil.column(AgentRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AgentRecord.class, "agentId"), "=", agentId)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, AgentRecord.class);
    }

    @Override
    public AgentRecord getByClientId(String clientId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentRecord.class))
                .where(DbUtil.column(AgentRecord.class, "clientId"), "=", clientId)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, AgentRecord.class);
    }

    @Override
    public List<AgentRecord> list(Long userId, String agentId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentRecord.class))
                .where(DbUtil.column(AgentRecord.class, "userId"), "=", userId);

        if(!StringUtil.isBlank(agentId)){
            sqlParamsBuilder.where(DbUtil.column(AgentRecord.class, "agentId"), "like", "%" + agentId + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(AgentRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, AgentRecord.class);
    }

    @Override
    public long count(Long userId, String agentId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentRecord.class))
                .where(DbUtil.column(AgentRecord.class, "userId"), "=", userId);
        if(!StringUtil.isBlank(agentId)){
            sqlParamsBuilder.where(DbUtil.column(AgentRecord.class, "agentId"), "like", "%" + agentId + "%");
        }
        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, AgentRecord.class);
    }

    @Override
    public boolean insert(AgentRecord agentRecord) {
        boolean inserted = this.dbManager.insert(agentRecord) > 0;
        return inserted;
    }

    @Override
    public boolean update(AgentRecord agentRecord) {
        boolean updated = this.dbManager.update(agentRecord) > 0;
        return updated;
    }

    @Override
    public boolean deleteByUserIdAndAgentId(Long userId, String agentId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(AgentRecord.class))
                .where(DbUtil.column(AgentRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(AgentRecord.class, "agentId"), "=", agentId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, AgentRecord.class) > 0;
        return deleted;
    }

    @Override
    public AgentVersionRecord getLatestVersion() {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(AgentVersionRecord.class))
                .order(DbUtil.column(AgentVersionRecord.class, "releaseTime"), OrderType.DESC)
                .page(1, 1);

        SqlParams sqlParams = sqlParamsBuilder.build();

        AgentVersionRecord agentVersionRecord = this.dbManager.selectFirstBySql(sqlParams.getSql(), AgentVersionRecord.class);
        return agentVersionRecord;
    }
}
