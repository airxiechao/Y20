package com.airxiechao.y20.pipeline.db.procedure;

import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.pipeline.db.api.IPipelineDb;
import com.airxiechao.y20.pipeline.db.record.*;
import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineWithLastRunVo;
import com.airxiechao.y20.project.db.record.ProjectRecord;

import java.util.Date;
import java.util.List;

public class PipelineDbProcedure extends AbstractDbProcedure implements IPipelineDb {

    public PipelineDbProcedure(DbManager dbManager){
        super(dbManager);
    }

    @Override
    public List<PipelineRecord> list(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(PipelineRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRecord.class, "projectId"), "=", projectId);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(AgentRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineRecord.class);
    }

    @Override
    public List<PipelineRecord> list(Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(PipelineRecord.class, "projectId"), "=", projectId);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRecord.class, "name"), "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(AgentRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineRecord.class);
    }

    @Override
    public List<PipelineRecord> listScheduledPipeline(Long fromPipelineId, Long toPipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(PipelineRecord.class, "flagCronEnabled"), "=", 1)
                .where(DbUtil.column(PipelineRecord.class, "id"), ">=", fromPipelineId)
                .where(DbUtil.column(PipelineRecord.class, "id"), "<", toPipelineId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineRecord.class);
    }

    @Override
    public List<PipelineWithLastRunVo> listWithLastRun(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("pipeline.*, R.status as last_status, R.id as last_pipeline_run_id, R.begin_time as last_begin_time, R.end_time as last_end_time")
                .from(DbUtil.table(PipelineRecord.class))
                .join("left join lateral " +
                        "( select id, pipeline_id, begin_time, end_time, status from pipeline_run where pipeline_run.pipeline_id = pipeline.id and pipeline_run.deleted = 0 order by begin_time desc limit 1 ) as R " +
                        "on R.pipeline_id = pipeline.id")
                .where("pipeline.deleted", "=", false)
                .where("pipeline.user_id", "=", userId)
                .where("pipeline.project_id", "=", projectId);

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where("pipeline.name", "like", "%" + name + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)) {
            String[] fields = orderField.split(",");
            for (String field : fields) {
                field = field.strip();
                if(field.equals("bookmarkTime")){
                    sqlParamsBuilder.order(DbUtil.column(ProjectRecord.class, field), OrderType.ASC);
                }else{
                    sqlParamsBuilder.order(DbUtil.column(PipelineWithLastRunVo.class, field), orderType);
                }
            }
        }

        if(null != pageNo && null != pageSize){
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineWithLastRunVo.class);
    }

    @Override
    public long count(Long userId, Long projectId, String name) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(PipelineRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRecord.class, "projectId"), "=", projectId);
        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRecord.class, "name"), "like", "%" + name + "%");
        }
        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, PipelineRecord.class);
    }

    @Override
    public boolean hasMore(Long fromPipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "deleted"), "=", false)
                .where(DbUtil.column(PipelineRecord.class, "id"), ">=", fromPipelineId);

        sqlParamsBuilder.page(1, 1);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return null != this.dbManager.selectFirstBySql(sqlParams, PipelineRecord.class);
    }

    @Override
    public boolean insert(PipelineRecord record) {
        boolean inserted = this.dbManager.insert(record) > 0;
        return inserted;
    }

    @Override
    public PipelineRecord get(Long userId, Long projectId, Long pipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRecord.class))
                .where(DbUtil.column(PipelineRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelineRecord.class, "id"), "=", pipelineId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineRecord.class);

    }

    @Override
    public PipelineRecord get(Long pipelineId) {
        return this.dbManager.getById(pipelineId, PipelineRecord.class);
    }

    @Override
    public boolean update(PipelineRecord record) {
        return this.dbManager.update(record) > 0;
    }

    @Override
    public List<PipelineStepTypeRecord> listStepType() {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineStepTypeRecord.class));

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineStepTypeRecord.class);
    }

    @Override
    public PipelineStepTypeRecord getStepType(String type) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineStepTypeRecord.class))
                .where(DbUtil.column(PipelineStepTypeRecord.class, "type"), "=", type);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineStepTypeRecord.class);
    }

    @Override
    public boolean createPipelineRun(PipelineRunRecord runRecord) {
        return dbManager.insert(runRecord) > 0;
    }

    @Override
    public PipelineRunRecord getPipelineRun(Long pipelineRunId) {
        return this.dbManager.getById(pipelineRunId, PipelineRunRecord.class);
    }

    @Override
    public PipelineRunRecord getPipelineRunByInstanceUuid(String pipelineRunInstanceUuid) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "instanceUuid"), "=", pipelineRunInstanceUuid);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRunRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelineRunRecord.class, "pipelineId"), "=", pipelineId)
                .where(DbUtil.column(PipelineRunRecord.class, "id"), "=", pipelineRunId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, String pipelineRunInstanceUuid) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRunRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelineRunRecord.class, "pipelineId"), "=", pipelineId)
                .where(DbUtil.column(PipelineRunRecord.class, "instanceUuid"), "=", pipelineRunInstanceUuid);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public boolean updatePipelineRun(PipelineRunRecord runRecord) {
        return this.dbManager.update(runRecord) > 0;
    }

    @Override
    public boolean createPipelineStepRun(PipelineStepRunRecord stepRunRecord) {
        return this.dbManager.insert(stepRunRecord) > 0;
    }

    @Override
    public List<PipelineStepRunRecord> listPipelineStepRun(Long pipelineRunId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineStepRunRecord.class))
                .where(DbUtil.column(PipelineStepRunRecord.class, "pipelineRunId"), "=", pipelineRunId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineStepRunRecord.class);
    }

    @Override
    public List<PipelineRunRecord> listPipelineRun(
            Long userId, Long projectId, Long pipelineId,  String name, String status, Boolean onlyRunning,
            String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRunRecord.class, "deleted"), "=", false);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "pipelineId"), "=", pipelineId);
        }

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "name"), "like", name + "%");
        }

        if(!StringUtil.isBlank(status)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "status"), "=", status);
        }

        if(null != onlyRunning && onlyRunning){
            sqlParamsBuilder.whereGroup(new SqlParamsBuilder.SqlWhereTripleGroup()
                    .and(DbUtil.column(PipelineRunRecord.class, "status"), "!=", EnumPipelineRunStatus.STATUS_FAILED)
                    .and(DbUtil.column(PipelineRunRecord.class, "status"), "!=", EnumPipelineRunStatus.STATUS_PASSED)
            );
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(PipelineRunRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public long countPipelineRun(Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRunRecord.class, "deleted"), "=", false);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "pipelineId"), "=", pipelineId);
        }

        if(!StringUtil.isBlank(name)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "name"), "like", name + "%");
        }

        if(!StringUtil.isBlank(status)){
            sqlParamsBuilder.where(DbUtil.column(PipelineRunRecord.class, "status"), "=", status);
        }

        if(null != onlyRunning && onlyRunning){
            sqlParamsBuilder.whereGroup(new SqlParamsBuilder.SqlWhereTripleGroup()
                    .and(DbUtil.column(PipelineRunRecord.class, "status"), "!=", EnumPipelineRunStatus.STATUS_FAILED)
                    .and(DbUtil.column(PipelineRunRecord.class, "status"), "!=", EnumPipelineRunStatus.STATUS_PASSED)
            );
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public long countPipelineRun(Long userId, Date beginTime, Date endTime) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineRunRecord.class))
                .where(DbUtil.column(PipelineRunRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineRunRecord.class, "beginTime"), ">=", beginTime)
                .where(DbUtil.column(PipelineRunRecord.class, "beginTime"), "<", endTime)
                .count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, PipelineRunRecord.class);
    }

    @Override
    public PipelineStepRunRecord getPipelineStepRun(Long pipelineRunId, int position) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineStepRunRecord.class))
                .where(DbUtil.column(PipelineStepRunRecord.class, "pipelineRunId"), "=", pipelineRunId)
                .where(DbUtil.column(PipelineStepRunRecord.class, "position"), "=", position);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineStepRunRecord.class);
    }

    @Override
    public boolean updatePipelineStepRun(PipelineStepRunRecord stepRunRecord) {
        return this.dbManager.update(stepRunRecord) > 0;
    }

    @Override
    public boolean createPipelineWebhookTrigger(PipelineWebhookTriggerRecord triggerRecord) {
        return this.dbManager.insert(triggerRecord) > 0;
    }

    @Override
    public boolean updatePipelineWebhookTrigger(PipelineWebhookTriggerRecord triggerRecord) {
        return this.dbManager.update(triggerRecord) > 0;
    }

    @Override
    public PipelineWebhookTriggerRecord getPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineWebhookTriggerRecord.class))
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "id"), "=", pipelineWebhookTriggerId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "pipelineId"), "=", pipelineId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelineWebhookTriggerRecord.class);
    }

    @Override
    public List<PipelineWebhookTriggerRecord> listPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineWebhookTriggerRecord.class))
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "userId"), "=", userId);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelineWebhookTriggerRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelineWebhookTriggerRecord.class, "pipelineId"), "=", pipelineId);
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(PipelineWebhookTriggerRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelineWebhookTriggerRecord.class);
    }

    @Override
    public long countPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelineWebhookTriggerRecord.class))
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "userId"), "=", userId);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelineWebhookTriggerRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelineWebhookTriggerRecord.class, "pipelineId"), "=", pipelineId);
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, PipelineWebhookTriggerRecord.class);
    }

    @Override
    public boolean deletePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(PipelineWebhookTriggerRecord.class))
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "id"), "=", pipelineWebhookTriggerId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelineWebhookTriggerRecord.class, "pipelineId"), "=", pipelineId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, PipelineWebhookTriggerRecord.class) > 0;
        return deleted;
    }

    @Override
    public boolean createPipelinePending(PipelinePendingRecord pendingRecord) {
        return this.dbManager.insert(pendingRecord) > 0;
    }

    @Override
    public PipelinePendingRecord getPipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelinePendingRecord.class))
                .where(DbUtil.column(PipelinePendingRecord.class, "id"), "=", pipelinePendingId)
                .where(DbUtil.column(PipelinePendingRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelinePendingRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelinePendingRecord.class, "pipelineId"), "=", pipelineId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelinePendingRecord.class);
    }

    @Override
    public PipelinePendingRecord getEarliestPipelinePending(Long userId, Long projectId, Long pipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelinePendingRecord.class))
                .where(DbUtil.column(PipelinePendingRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelinePendingRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelinePendingRecord.class, "pipelineId"), "=", pipelineId)
                .order(DbUtil.column(PipelinePendingRecord.class, "createTime"), OrderType.ASC)
                .page(1, 1);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PipelinePendingRecord.class);
    }

    @Override
    public List<PipelinePendingRecord> listPipelinePending(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelinePendingRecord.class))
                .where(DbUtil.column(PipelinePendingRecord.class, "userId"), "=", userId);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelinePendingRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelinePendingRecord.class, "pipelineId"), "=", pipelineId);
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(DbUtil.column(PipelinePendingRecord.class, orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PipelinePendingRecord.class);
    }

    @Override
    public long countPipelinePending(Long userId, Long projectId, Long pipelineId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PipelinePendingRecord.class))
                .where(DbUtil.column(PipelinePendingRecord.class, "userId"), "=", userId);

        if(null != projectId) {
            sqlParamsBuilder.where(DbUtil.column(PipelinePendingRecord.class, "projectId"), "=", projectId);
        }

        if(null != pipelineId){
            sqlParamsBuilder.where(DbUtil.column(PipelinePendingRecord.class, "pipelineId"), "=", pipelineId);
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, PipelinePendingRecord.class);
    }

    @Override
    public boolean deletePipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(PipelinePendingRecord.class))
                .where(DbUtil.column(PipelinePendingRecord.class, "id"), "=", pipelinePendingId)
                .where(DbUtil.column(PipelinePendingRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PipelinePendingRecord.class, "projectId"), "=", projectId)
                .where(DbUtil.column(PipelinePendingRecord.class, "pipelineId"), "=", pipelineId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, PipelinePendingRecord.class) > 0;
        return deleted;
    }

}
