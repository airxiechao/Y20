package com.airxiechao.y20.project.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.project.db.api.IProjectDb;
import com.airxiechao.y20.project.db.record.ProjectRecord;

import java.util.List;

public class ProjectDbProcedure extends AbstractDbProcedure implements IProjectDb {

    public ProjectDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public List<ProjectRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ProjectRecord.class))
                .where(DbUtil.column(ProjectRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(ProjectRecord.class, "userId"), "=", userId);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(ProjectRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)) {
            String[] fields = orderField.split(",");
            for (String field : fields) {
                field = field.strip();
                if(field.equals("bookmarkTime")){
                    sqlParamsBuilder.order(DbUtil.column(ProjectRecord.class, field), OrderType.ASC);
                }else{
                    sqlParamsBuilder.order(DbUtil.column(ProjectRecord.class, field), orderType);
                }
            }
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, ProjectRecord.class);
    }

    @Override
    public long count(Long userId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ProjectRecord.class))
                .where(DbUtil.column(ProjectRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(ProjectRecord.class, "userId"), "=", userId);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(ProjectRecord.class, "name"), "like", "%" + name + "%");
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, ProjectRecord.class);
    }

    @Override
    public ProjectRecord getById(Long userId, Long projectId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(ProjectRecord.class))
                .where(DbUtil.column(ProjectRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(ProjectRecord.class, "id"), "=", projectId)
                .build();

        return this.dbManager.selectFirstBySql(sqlParams, ProjectRecord.class);
    }

    @Override
    public boolean create(ProjectRecord record) {
        return dbManager.insert(record) > 0;
    }

    @Override
    public boolean update(ProjectRecord record) {
        return dbManager.update(record) > 0;
    }
}
