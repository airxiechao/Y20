package com.airxiechao.y20.test;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.monitor.PipelineRunMonitor;
import com.airxiechao.y20.pipeline.pojo.Pipeline;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.CreatePipelineRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.param.StartPipelineRunInstanceParam;
import com.airxiechao.y20.pipeline.rest.server.PipelineRestServer;
import com.airxiechao.y20.pipeline.run.step.param.RemotePrepareEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemoteScriptStepRunParam;

public class PipelineTest {

    public static void main(String[] args) throws Exception {
        PipelineRestServer.getInstance().start();

        IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

        Pipeline pipeline = new Pipeline()
                .addStep(
                        new PipelineStep()
                                .setType(EnumStepRunType.REMOTE_PREPARE_ENV_TYPE)
                                .setParams(ModelUtil.toMapAndCheckRequiredField(new RemotePrepareEnvStepRunParam(
                                        "a",
                                        "maven:3.6.3-jdk-11",
                                        null,
                                        null,
                                        null,
                                        null,
                                        false,
                                        null,
                                        false,
                                        false
                                )))
                )
                .addStep(
                        new PipelineStep()
                                .setType(EnumStepRunType.REMOTE_SCRIPT_TYPE)
                                .setParams(ModelUtil.toMapAndCheckRequiredField(new RemoteScriptStepRunParam(
                                        "for i in 1 2 3 4 5; do echo \"Hit $i\"; sleep 1; done",
                                        null,
                                        null
                                )))
                );

        pipeline.setUserId(4L);

        //PipelineRecord pipelineRecord = pipelineBiz.create(pipeline);
        PipelineRunRecord runRecord = pipelineBiz.createPipelineRun(pipeline, "test", null, false);

        Response createRunInstanceResp = ServiceRestClient.get(IServicePipelineRunInstanceRest.class).createPipelineRunInstance(
                new CreatePipelineRunInstanceParam(runRecord.getId()));

        // monitor
        PipelineRunMonitor pipelineRunMonitor = new PipelineRunMonitor(runRecord.getId());

        pipelineRunMonitor.addPipelineRunStatusUpdateListener((status) -> {
            System.out.println("pipeline run -> " + status);
        });
        pipelineRunMonitor.addStepRunStatusUpdateListener((position, status) -> {
            System.out.println("step run " + position + " -> " + status);
        });
        pipelineRunMonitor.addStepRunOutputListener((position, output) -> {
            System.out.print(output);
        });
        pipelineRunMonitor.start();

        String pipelineRunInstanceUuid = (String)createRunInstanceResp.getData();
        Response startRunInstanceResp = ServicePipelineRunInstanceRestClient.get(IServicePipelineRunInstanceRest.class, pipelineRunInstanceUuid).startPipelineRunInstance(
                new StartPipelineRunInstanceParam(pipelineRunInstanceUuid, false));

//        IPipelineRunInstance runInstance = pipelineRunInstanceBiz.createRunInstance(runRecord);
//        BiConsumer<Integer, String> stepRunOutputListener = (position, output) -> {
//            System.out.print(output);
//        };
//        runInstance.addStepRunOutputListener(stepRunOutputListener);
//        runInstance.startAsync();

    }
}
