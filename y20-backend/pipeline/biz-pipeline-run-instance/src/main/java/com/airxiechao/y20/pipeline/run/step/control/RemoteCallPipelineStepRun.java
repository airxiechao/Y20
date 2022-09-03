package com.airxiechao.y20.pipeline.run.step.control;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.biz.api.IPipelineRunInstanceBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineStepRunStatus;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunCategory;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.pubsub.event.step.EventStepRunOutputPush;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.RemoteCallPipelineStepRunParam;

import java.util.HashMap;
import java.util.Map;

@StepRun(
        visible = true,
        order = 1,
        type = EnumStepRunType.REMOTE_CALL_PIPELINE_TYPE,
        name = EnumStepRunType.REMOTE_CALL_PIPELINE_NAME,
        category = EnumStepRunCategory.CONTROL,
        icon = "functions",
        description = "运行其他流水线",
        paramClass = RemoteCallPipelineStepRunParam.class,
        outputs = { "被调用流水线的输出变量（带前缀）" }
)
public class RemoteCallPipelineStepRun extends RemoteStepRunInstance {

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);
    private IPipelineRunInstanceBiz pipelineRunInstanceBiz = Biz.get(IPipelineRunInstanceBiz.class);
    private RemoteCallPipelineStepRunParam param;
    private IPipelineRunInstance remotePipelineRunInstance;

    public RemoteCallPipelineStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteCallPipelineStepRunParam.class);
    }

    @Override
    public Response run() throws Exception {
        String runName = String.format("调用 from:/project/%d/pipeline/%d/run/%d", env.getProjectId(), env.getPipelineId(), env.getPipelineRunId());
        return triggerPipelineRun(env.getUserId(), param.getProjectId(), param.getPipelineId(),
                runName, param.getInParams(), false);
    }

    @Override
    public Response stop() throws Exception {
        if(null != remotePipelineRunInstance){
            remotePipelineRunInstance.stopAsync();
        }
        return new Response();
    }

    private Response triggerPipelineRun(
            Long userId, Long projectId, Long pipelineId,
            String name, Map<String, String> inParams, Boolean flagDebug
    ) throws Exception{
        // create run record
        PipelineRunRecord pipelineRunRecord = pipelineBiz.createPipelineRun(
                userId, projectId, pipelineId,
                name, inParams, flagDebug);
        if(null == pipelineRunRecord){
            throw new Exception("create pipeline run error");
        }

        Long pipelineRunId = pipelineRunRecord.getId();

        try{
            // create run instance
            PipelineRunDetailVo pipelineRunDetail = pipelineBiz.getPipelineRunVo(pipelineRunRecord.getUserId(), pipelineRunRecord.getProjectId(), pipelineRunRecord.getPipelineId(), pipelineRunRecord.getId());
            remotePipelineRunInstance = pipelineRunInstanceBiz.createRunInstance(pipelineRunDetail);

            // add pipeline run status listener
            remotePipelineRunInstance.addPipelineRunStatusUpdateListener((pipelineRunInstanceUuid, status, error, outParams) -> {
                switch (status){
                    case EnumPipelineRunStatus.STATUS_STARTED:
                        pushLog(String.format("pipeline run start -> https://y20.work/nav/project/%d/pipeline/%d/run/%d", projectId, pipelineId, pipelineRunId), true);
                        break;
                    case EnumPipelineRunStatus.STATUS_PASSED:
                        pushLog("pipeline run passed", true);
                        Map<String, String> outParamsWithPrefix = new HashMap<>();
                        String prefix = param.getOutParamPrefix();
                        if(null != outParams){
                            outParams.entrySet().forEach(entry -> {
                                String outName = prefix + entry.getKey();
                                String outValue = entry.getValue();
                                outParamsWithPrefix.put(outName, outValue);
                            });
                        }
                        pipelineRunInstanceBiz.stepRunInstanceCallback(env.getPipelineRunInstanceUuid(), stepRunInstanceUuid, new Response().data(outParamsWithPrefix));
                        break;
                    case EnumPipelineRunStatus.STATUS_FAILED:
                        pushLog(String.format("pipeline run failed: %s", error), true);
                        pipelineRunInstanceBiz.stepRunInstanceCallback(env.getPipelineRunInstanceUuid(), stepRunInstanceUuid, new Response().error(error));
                        break;
                }
            });

            // step run status
            remotePipelineRunInstance.addStepRunStatusUpdateListener((position, stepRunInstanceUuid, status, error) -> {
                switch (status){
                    case EnumPipelineStepRunStatus.STATUS_STARTED:
                        pushLog(String.format("step %d start... ", position+1), false);
                        break;
                    case EnumPipelineStepRunStatus.STATUS_PASSED:
                        pushLog("passed", true);
                        break;
                    case EnumPipelineStepRunStatus.STATUS_FAILED:
                        pushLog(String.format("failed: %s", error), true);
                        break;
                    case EnumPipelineStepRunStatus.STATUS_SKIPPED:
                        pushLog(String.format("skipped: %s", error), true);
                        break;
                }
            });

            // start run instance
            remotePipelineRunInstance.startAsync();

            return new Response();
        }catch (Exception e){
            pipelineBiz.updatePipelineRunStatus(pipelineRunId, EnumPipelineRunStatus.STATUS_FAILED, e.getMessage(), null);
            return new Response().error(e.getMessage());
        }
    }

    private void pushLog(String output, boolean newLine){
        if(newLine){
            output += "\r\n";
        }

        EventBus.getInstance().publish(
                new EventStepRunOutputPush(env.getPipelineRunInstanceUuid(), stepRunInstanceUuid, output));

    }
}
