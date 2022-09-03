package com.airxiechao.y20.pipeline.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.storage.cache.redis.RedisLock;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.cache.PipelineRunInstanceServiceTagCache;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.biz.api.IPipelineRunInstanceBiz;
import com.airxiechao.y20.pipeline.db.record.PipelinePendingRecord;
import com.airxiechao.y20.pipeline.lock.PipelinePendingLockFactory;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelinePending;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineInjectName;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineStepRunStatus;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.pubsub.event.pipeline.*;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCreatePipelineRunParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceStartPipelineRunParam;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;
import com.airxiechao.y20.pipeline.run.pipeline.PipelineRunInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class PipelineRunInstanceBizProcess implements IPipelineRunInstanceBiz {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunInstanceBizProcess.class);
    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    @Override
    public boolean stepRunInstanceCallback(String pipelineRunInstanceUuid, String stepRunInstanceUuid, Response response) {
        IPipelineRunInstance pipelineRunInstance = PipelineRunInstanceFactory.getInstance().getPipelineRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            return false;
        }

        AbstractStepRunInstance stepRunInstance = pipelineRunInstance.getStepRunInstance(stepRunInstanceUuid);
        if(null == stepRunInstance){
            return false;
        }

        pipelineRunInstance.stepRunInstanceCallback(stepRunInstanceUuid, response);

        return true;
    }

    @Override
    public List<IPipelineRunInstance> listRunInstance() {
        return PipelineRunInstanceFactory.getInstance().listPipelineRunInstance();
    }

    @Override
    public IPipelineRunInstance createRunInstance(PipelineRunDetailVo pipelineRunDetail) {
        return buildRunInstance(pipelineRunDetail);
    }

    @Override
    public IPipelineRunInstance getRunInstance(String pipelineRunInstanceUuid) {
        return PipelineRunInstanceFactory.getInstance().getPipelineRunInstance(pipelineRunInstanceUuid);
    }

    private IPipelineRunInstance buildRunInstance(PipelineRunDetailVo pipelineRunDetail){
        IPipelineRunInstance pipelineRunInstance = PipelineRunInstanceFactory.getInstance().buildPipelineRunInstance(pipelineRunDetail);
        PipelineRun pipelineRun = pipelineRunDetail.getPipelineRun();
        Long pipelineRunId = pipelineRun.getPipelineRunId();

        // pipeline run status
        pipelineRunInstance.addPipelineRunStatusUpdateListener((pipelineRunInstanceUuid, status, error, outParams) -> {
            switch (status){
                case EnumPipelineRunStatus.STATUS_STARTED:
                case EnumPipelineRunStatus.STATUS_WAITING:
                case EnumPipelineRunStatus.STATUS_RUNNING:
                    onPipelineRunStatusUpdate(pipelineRunId, status, null, null);
                    break;
                case EnumPipelineRunStatus.STATUS_PASSED:
                case EnumPipelineRunStatus.STATUS_FAILED:
                    onPipelineRunStatusUpdate(pipelineRunId, status, error, outParams);
                    PipelineRunInstanceFactory.getInstance().destroyPipelineRunInstance(pipelineRunInstanceUuid);

                    // remove service tag
                    PipelineRunInstanceServiceTagCache.getInstance().removePipelineRunInstanceServiceTag(pipelineRunInstanceUuid);

                    break;
            }
        });

        // step run status
        pipelineRunInstance.addStepRunStatusUpdateListener((position, stepRunInstanceUuid, status, error) -> {
            switch (status){
                case EnumPipelineStepRunStatus.STATUS_STARTED:
                    onPipelineStepRunStatusUpdate(pipelineRunId, position, status, null);
                    break;
                case EnumPipelineStepRunStatus.STATUS_PASSED:
                case EnumPipelineStepRunStatus.STATUS_FAILED:
                case EnumPipelineStepRunStatus.STATUS_SKIPPED:
                    onPipelineStepRunStatusUpdate(pipelineRunId, position, status, error);
                    break;
            }
        });

        // step output
        pipelineRunInstance.addStepRunOutputListener((position, output) -> {
            onPipelineStepRunOutput(pipelineRunId, position, output);
        });

        // terminal run status
        pipelineRunInstance.addTerminalRunStatusUpdateListener((terminalRunInstanceUuid, status) -> {
            onTerminalRunStatusUpdate(pipelineRunId, terminalRunInstanceUuid, status);
        });

        // terminal output
        pipelineRunInstance.addTerminalRunOutputListener((terminalRunInstanceUuid, output) -> {
            onTerminalRunOutput(pipelineRunId, terminalRunInstanceUuid, output);
        });

        // explorer run status
        pipelineRunInstance.addExplorerRunStatusUpdateListener((explorerRunInstanceUuid, status) -> {
            onExplorerRunStatusUpdate(pipelineRunId, explorerRunInstanceUuid, status);
        });

        // explorer run transfer progress
        pipelineRunInstance.addExplorerRunTransferProgressListener((explorerRunInstanceUuid, path, direction, total, speed, progress) -> {
            onExplorerTransferProgress(pipelineRunId, explorerRunInstanceUuid, path, direction, total, speed, progress);
        });

        // register service tag
        String pipelineRunInstanceRestServerUuid = Global.getInstance().inject(EnumPipelineInjectName.PIPELINE_RUN_INSTANCE_REST_SERVER);
        PipelineRunInstanceServiceTagCache.getInstance().setPipelineRunInstanceServiceTag(
                pipelineRunInstance.getPipelineRunInstanceUuid(), pipelineRunInstanceRestServerUuid);

        return pipelineRunInstance;
    }

    private boolean onPipelineRunStatusUpdate(Long pipelineRunId, String status, String error, Map<String, String> outParams) {
        boolean updated = pipelineBiz.updatePipelineRunStatus(pipelineRunId, status, error, outParams);
        if(updated){
            // pipeline run status update
            EventBus.getInstance().publish(
                    new EventPipelineRunStatusUpdate(pipelineRunId, status, error, outParams));

            // publish activity and check pending
            switch (status){
                case EnumPipelineRunStatus.STATUS_PASSED:
                case EnumPipelineRunStatus.STATUS_FAILED:
                    PipelineRun pipelineRun = pipelineBiz.getPipelineRun(pipelineRunId).toPojo();
                    Pipeline pipeline = pipelineRun.getPipeline();

                    // activity
                    EventBus.getInstance().publish(
                            new EventPipelineRunActivity(
                                    pipelineRun.getUserId(),
                                    pipelineRun.getProjectId(),
                                    pipeline.getPipelineId(),
                                    pipeline.getName(),
                                    pipelineRunId,
                                    pipelineRun.getName(),
                                    status,
                                    error,
                                    pipelineRun.getBeginTime(),
                                    pipelineRun.getEndTime()
                            ));

                    // dequeue pending
                    try( RedisLock lock = PipelinePendingLockFactory.getInstance().get(pipeline.getPipelineId(), 60)) {
                        // try to get lock
                        if (lock.tryLock()) {
                            PipelinePendingRecord pendingRecord = pipelineBiz.getEarliestPipelinePending(
                                    pipeline.getUserId(), pipeline.getProjectId(), pipeline.getPipelineId());
                            if(null != pendingRecord){
                                PipelinePending pending = pendingRecord.toPojo();
                                dequeuePipelinePending(pending);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("get pipeline [{}] pending lock error", pipeline.getPipelineId(), e);
                    }

                    break;
            }

        }
        return updated;
    }

    private boolean onPipelineStepRunStatusUpdate(Long pipelineRunId, Integer position, String status, String error) {
        boolean updated = pipelineBiz.updatePipelineStepRunStatus(pipelineRunId, position, status, error);
        if(updated){
            EventBus.getInstance().publish(
                    new EventPipelineStepRunStatusUpdate(pipelineRunId, position, status, error));
        }
        return updated;
    }

    private void onPipelineStepRunOutput(Long pipelineRunId, Integer position, String output){
        EventBus.getInstance().publish(
                new EventPipelineStepRunOutput(pipelineRunId, position, output));
    }

    private void onTerminalRunStatusUpdate(Long pipelineRunId, String terminalRunInstanceUuid, String status) {
        EventBus.getInstance().publish(
                new EventPipelineTerminalRunStatusUpdate(pipelineRunId, terminalRunInstanceUuid, status));
    }

    private void onTerminalRunOutput(Long pipelineRunId, String terminalRunInstanceUuid, String output){
        EventBus.getInstance().publish(
                new EventPipelineTerminalRunOutput(pipelineRunId, terminalRunInstanceUuid, output));
    }

    private void onExplorerRunStatusUpdate(Long pipelineRunId, String explorerRunInstanceUuid, String status) {
        EventBus.getInstance().publish(
                new EventPipelineExplorerRunStatusUpdate(pipelineRunId, explorerRunInstanceUuid, status));
    }

    private void onExplorerTransferProgress(Long pipelineRunId, String explorerRunInstanceUuid,
                                            String path, String direction, Long total, Long speed, Double progress){
        EventBus.getInstance().publish(
                new EventPipelineExplorerRunTransferProgress(
                        pipelineRunId, explorerRunInstanceUuid, path, direction, total, speed, progress));
    }

    private void dequeuePipelinePending(PipelinePending pending){
        logger.info("dequeue pipeline pending [{}]", pending.getPipelineId());

        Response<Long> createResp =  ServiceRestClient.get(IServicePipelineRest.class).createPipelineRun(
                new ServiceCreatePipelineRunParam(
                        pending.getPipelineId(),
                        pending.getName(),
                        pending.getInParams(),
                        pending.getFlagDebug()
                ));

        if(!createResp.isSuccess()){
            logger.error("create pending pipeline [{}] run error: {}", pending.getPipelineId(), createResp.getMessage());
            return;
        }

        // remove pending
        boolean pendingDeleted = pipelineBiz.deletePipelinePending(pending.getUserId(), pending.getProjectId(), pending.getPipelineId(), pending.getPipelinePendingId());
        if(!pendingDeleted){
            logger.error("delete pipeline pending [{}] error: {}", pending.getPipelinePendingId());
        }

        Long pipelineRunId = createResp.getData();
        Response<Long> startResp =  ServiceRestClient.get(IServicePipelineRest.class).startPipelineRun(
                new ServiceStartPipelineRunParam(pipelineRunId));

        if(!startResp.isSuccess()){
            logger.error("start pending pipeline [{}] run [{}] error: {}", pending.getPipelineId(), pipelineRunId, startResp.getMessage());
            return;
        }

        logger.info("start pending pipeline [{}] run [{}] complete", pending.getPipelinePendingId(), pipelineRunId);
    }
}
