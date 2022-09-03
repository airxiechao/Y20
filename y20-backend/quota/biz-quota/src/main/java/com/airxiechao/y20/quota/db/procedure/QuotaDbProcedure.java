package com.airxiechao.y20.quota.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.quota.db.api.IQuotaDb;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;

import java.util.Date;
import java.util.List;

public class QuotaDbProcedure  extends AbstractDbProcedure implements IQuotaDb {

    public QuotaDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public QuotaPieceRecord getAggregatedQuota(Long userId, Date time) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("sum(max_agent) as max_agent, sum(max_pipeline_run) as max_pipeline_run, min(begin_time) as begin_time, min(end_time) as end_time")
                .from(DbUtil.table(QuotaPieceRecord.class))
                .where(DbUtil.column(QuotaPieceRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(QuotaPieceRecord.class, "beginTime"), "<=", time)
                .where(DbUtil.column(QuotaPieceRecord.class, "endTime"), ">", time);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, QuotaPieceRecord.class);
    }

    @Override
    public boolean createQuota(QuotaPieceRecord quotaPieceRecord) {
        return this.dbManager.insert(quotaPieceRecord) > 0;
    }

    @Override
    public List<QuotaPieceRecord> listQuota(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(QuotaPieceRecord.class))
                .where(DbUtil.column(QuotaPieceRecord.class, "userId"), "=", userId);

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(QuotaPieceRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, QuotaPieceRecord.class);
    }

    @Override
    public long countQuota(Long userId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(QuotaPieceRecord.class))
                .where(DbUtil.column(QuotaPieceRecord.class, "userId"), "=", userId)
                .count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, QuotaPieceRecord.class);
    }
}
