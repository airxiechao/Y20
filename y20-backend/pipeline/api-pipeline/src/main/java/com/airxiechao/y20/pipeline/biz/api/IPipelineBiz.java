package com.airxiechao.y20.pipeline.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pipeline.db.record.*;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineWithLastRunVo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IBiz
public interface IPipelineBiz {
    List<PipelineRecord> list(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);
    List<PipelineRecord> listScheduledPipeline(Long fromPipelineId, Long toPipelineId);
    List<PipelineWithLastRunVo> listWithLastRun(Long userId, Long projectId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize);

    long count(Long userId, Long projectId, String name);
    boolean hasMore(Long fromPipelineId);

    PipelineRecord create(Pipeline pipeline);

    PipelineRecord create(Long userId, Long projectId, String name, String description, Boolean flagOneRun, List<PipelineStep> steps, Map<String, PipelineVariable> variables);

    PipelineRecord copy(Long userId, Long projectId, Long pipelineId, Long toProjectId, String toPipelineName);

    PipelineRecord get(Long userId, Long projectId, Long pipelineId);

    PipelineRecord get(Long pipelineId);

    boolean update(Long userId, Long projectId, Long pipelineId, String name, String description, Boolean flagOneRun, Boolean flagInjectProjectVariable,
                   Boolean flagCronEnabled, Date cronBeginTime, Integer cronIntervalSecs, Map<String, String> cronInParams);
    boolean updateBookmark(Long userId, Long projectId, Long pipelineId, boolean bookmarked);
    boolean updatePublishedTemplate(Long userId, Long projectId, Long pipelineId, Long publishedTemplateId);
    boolean delete(Long userId, Long projectId, Long pipelineId);
    boolean delete(Long projectId);

    // step
    boolean addStep(Long userId, Long projectId, Long pipelineId, Integer position, PipelineStep step);

    boolean copyStep(Long userId, Long projectId, Long pipelineId, Integer srcPosition, Integer destPosition);

    boolean moveStep(Long userId, Long projectId, Long pipelineId, int srcPosition, int destPosition);

    boolean disableStep(Long userId, Long projectId, Long pipelineId, int position, boolean disabled);
    boolean updateStepCondition(Long userId, Long projectId, Long pipelineId, int position, String condition);

    boolean updateStep(Long userId, Long projectId, Long pipelineId, int position, PipelineStep step);

    boolean deleteStep(Long userId, Long projectId, Long pipelineId, int position);

    List<PipelineStepTypeRecord> listStepType();

    PipelineStepTypeRecord getStepType(String type);

    // run
    PipelineRunRecord createPipelineRun(Long userId, Long projectId, Long pipelineId, String name, Map<String, String> inParams, Boolean flagDebug) throws Exception;

    PipelineRunRecord createPipelineRun(Pipeline pipeline, String name, Map<String, String> inParams, Boolean flagDebug) throws Exception;

    PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId);

    PipelineRunRecord getPipelineRun(Long userId, Long projectId, Long pipelineId, String pipelineRunInstanceUuid);

    PipelineRunRecord getPipelineRun(Long pipelineRunId);

    PipelineRunRecord getPipelineRunByInstanceUuid(String pipelineRunInstanceUuid);

    Set<String> listPipelineRunRunningAgent(Long userId);

    boolean updatePipelineRunStatus(Long pipelineRunId, String status, String error, Map<String, String> outParams);

    boolean deletePipelineRun(Long userId, Long projectId, Long pipelineId, Long pipelineRunId);

    List<PipelineStepRunRecord> listPipelineStepRun(Long pipelineRunId);

    PipelineRunDetailVo getPipelineRunVo(Long userId, Long projectId, Long pipelineId, Long pipelineRunId);
    PipelineRunDetailVo getPipelineRunVoDesensitized(Long userId, Long projectId, Long pipelineId, Long pipelineRunId);

    List<PipelineRunRecord> listPipelineRun(
            Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning,
            String orderField, String orderType, Integer pageNo, Integer pageSize);

    long countPipelineRun(Long userId, Long projectId, Long pipelineId, String name, String status, Boolean onlyRunning);
    long countPipelineRun(Long userId, Date beginTime, Date endTime);

    boolean updatePipelineStepRunStatus(Long pipelineRunId, int position, String status, String error);

    // variable

    boolean createVariable(Long userId, Long projectId, Long pipelineId, PipelineVariable variable);

    boolean updateVariable(Long userId, Long projectId, Long pipelineId, PipelineVariable variable);

    boolean deleteVariable(Long userId, Long projectId, Long pipelineId, String variableName);

    // webhook trigger

    PipelineWebhookTriggerRecord createPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String sourceType, String eventType, String tag, Map<String, String> inParams);
    boolean updatePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId, String sourceType, String eventType, String tag, Map<String, String> inParams);
    boolean updatePipelineWebhookTriggerLastTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId, Date lastTriggerTime, Long lastTriggerPipelineRunId);
    PipelineWebhookTriggerRecord getPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId);
    List<PipelineWebhookTriggerRecord> listPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countPipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId);
    boolean deletePipelineWebhookTrigger(Long userId, Long projectId, Long pipelineId, Long pipelineWebhookTriggerId);

    // queue

    PipelinePendingRecord createPipelinePending(Long userId, Long projectId, Long pipelineId, String name, Map<String, String> inParams, Boolean flagDebug, String createReason);
    PipelinePendingRecord getPipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelineQueueId);
    PipelinePendingRecord getEarliestPipelinePending(Long userId, Long projectId, Long pipelineId);
    List<PipelinePendingRecord> listPipelinePending(Long userId, Long projectId, Long pipelineId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countPipelinePending(Long userId, Long projectId, Long pipelineId);
    boolean deletePipelinePending(Long userId, Long projectId, Long pipelineId, Long pipelineQueueId);

}