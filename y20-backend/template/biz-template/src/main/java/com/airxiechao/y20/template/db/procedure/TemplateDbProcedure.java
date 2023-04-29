package com.airxiechao.y20.template.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.template.db.api.ITemplateDb;
import com.airxiechao.y20.template.db.record.TemplateRecord;

import java.util.Arrays;
import java.util.List;

public class TemplateDbProcedure extends AbstractDbProcedure implements ITemplateDb {

    public TemplateDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public TemplateRecord get(Long userId, Long templateId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TemplateRecord.class))
                .where(DbUtil.column(TemplateRecord.class, "id"), "=", templateId);

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(TemplateRecord.class, "userId"), "=", userId);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, TemplateRecord.class);
    }

    @Override
    public List<TemplateRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder();

        String username = null;
        if(!StringUtil.isBlank(name) && name.startsWith("@")){
            username = name.substring(1);
        }

        boolean fulltextSearch = !StringUtil.isBlank(name) && !name.startsWith("@");
        if(fulltextSearch){
            sqlParamsBuilder.select("template.*, match(name,description) against(#{PN_NAME}) as score");
        }else{
            sqlParamsBuilder.select("*");
        }

        sqlParamsBuilder.from(DbUtil.table(TemplateRecord.class));

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(TemplateRecord.class, "userId"), "=", userId);
        }

        if(null != username) {
            sqlParamsBuilder.where(DbUtil.column(TemplateRecord.class, "username"), "=", username);
        }

        if(fulltextSearch){
            sqlParamsBuilder.where(DbUtil.columns(TemplateRecord.class, new String[]{"name", "description"}), null, name, true);
        }

        if(fulltextSearch){
            sqlParamsBuilder.order("score", OrderType.DESC);
        }else{
            if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
                sqlParamsBuilder.order(DbUtil.column(TemplateRecord.class, orderField), orderType);
            }
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        if(fulltextSearch){
            sqlParams.getParams().put("PN_NAME", name);
        }

        return this.dbManager.selectBySql(sqlParams, TemplateRecord.class);
    }

    @Override
    public long count(Long userId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder();

        String username = null;
        if(!StringUtil.isBlank(name) && name.startsWith("@")){
            username = name.substring(1);
        }

        boolean fulltextSearch = !StringUtil.isBlank(name) && !name.startsWith("@");

        sqlParamsBuilder.from(DbUtil.table(TemplateRecord.class));

        if(null != userId) {
            sqlParamsBuilder.where(DbUtil.column(TemplateRecord.class, "userId"), "=", userId);
        }

        if(null != username) {
            sqlParamsBuilder.where(DbUtil.column(TemplateRecord.class, "username"), "=", username);
        }

        if(fulltextSearch){
            sqlParamsBuilder.where(DbUtil.columns(TemplateRecord.class, new String[]{"name", "description"}), null, name, true);
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, TemplateRecord.class);
    }

    @Override
    public boolean insert(TemplateRecord templateRecord) {
        return dbManager.insert(templateRecord) > 0;
    }

    @Override
    public boolean update(TemplateRecord templateRecord) {
        return dbManager.update(templateRecord) > 0;
    }

    @Override
    public boolean updateNumApply(TemplateRecord templateRecord) {
        return dbManager.updateFields(templateRecord, Arrays.asList(
                DbUtil.column(TemplateRecord.class, "numApply")
        )) > 0;
    }

    @Override
    public boolean delete(long templateId) {
        return dbManager.deleteById(templateId, TemplateRecord.class) > 0;
    }
}
