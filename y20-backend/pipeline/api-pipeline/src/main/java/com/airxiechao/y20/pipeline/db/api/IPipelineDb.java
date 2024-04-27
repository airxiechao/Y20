package com.airxiechao.y20.pipeline.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.pipeline.db.record.*;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineWithLastRunVo;

import java.util.Date;
import java.util.List;

@IDb("mybatis-y20-pipeline.xml")
public interface IPipelineDb {

    List<PipelineRecord> list(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);
    List<PipelineRecord> list(Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);
    List<PipelineRecord> listScheduledPipeline(Long fromPipelineId, Long toPipelineId);
    List<PipelineWithLastRunVo> listWithLastRun(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long count(Long userId, Long projectId, String name);
    boolean hasMore(Long fromPipelineId);
    boolean insert(PipelineRecord record);
    PipelineRecord get(Long userId, Long projectId, Long pipelineId);
    PipelineRecord get(Long pipelineId);
    boolean update(PipelineRecord record);

    // step
    List<PipelineStepTypeRecord> listStepType();
    PipelineStepTypeRecord getStepType(String type);

    // run
    boolean createPipelineRun(PipelineRunRecord runRecord);
    PipelineRunRecord getPipelineRun(Long pipelineRunId);
    PipelineRunRecord getPipelineRunByInstanceUuid(String pipelineRunInstanceUuid);
    PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId);
    PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, String pipelineRunInstanceUuid);
    boolean updatePipelineRun(PipelineRunRecord runRecord);

    boolean createPipelineStepRun(PipelineStepRunRecord stepRunRecord);
    List<PipelineStepRunRecord> listPipelineStepRun(Long pipelineRunId);
    List<PipelineRunRecord> listPipelineRun(Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning, Boolean noPseudo, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countPipelineRun(Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning, Boolean noPseudo);
    long countPipelineRun(Long userId, Date beginTime, Date endTime);

    PipelineStepRunRecord getPipelineStepRun(Long pipelineRunId, int position);
    boolean updatePipelineStepRun(PipelineStepRunRecord stepRunRecord);

    // webhook trigger
    boolean createPipelineWebhookTrigger(PipelineWebhookTriggerRecord triggerRecord);
    boolean updatePipelineWebhookTrigger(PipelineWebhookTriggerRecord triggerRecord);
    PipelineWebhookTriggerRecord getPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId);
    List<PipelineWebhookTriggerRecord> listPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId);
    boolean deletePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId);

    // pending
    boolean createPipelinePending(PipelinePendingRecord pendingRecord);
    PipelinePendingRecord getPipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId);
    PipelinePendingRecord getEarliestPipelinePending(Long userId, Long projectId, Long pipelineId);
    List<PipelinePendingRecord> listPipelinePending(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countPipelinePending(Long userId, Long projectId, Long pipelineId);
    boolean deletePipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelinePendingId);

}
