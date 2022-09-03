package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.REMOTE_DESTROY_ENV_TYPE,
        name = EnumStepRunType.REMOTE_DESTROY_ENV_NAME,
        paramClass = RemoteStopEnvStepRunParam.class)
public class RemoteDestroyEnvStepRun extends RemoteStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(RemoteDestroyEnvStepRun.class);

    private RemoteDestroyEnvStepRunParam param;

    public RemoteDestroyEnvStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteDestroyEnvStepRunParam.class);

        PipelineStep prepareStep = new PipelineStep();
        if(!StringUtil.isBlank(env.getDockerContainerId())){
            prepareStep.setType(EnumStepRunType.DESTROY_DOCKER_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new DestroyDockerEnvStepRunParam(env.getEnvInstanceUuid())));
        }else{
            prepareStep.setType(EnumStepRunType.DESTROY_NATIVE_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new DestroyNativeEnvStepRunParam(env.getEnvInstanceUuid())));
        }

        this.step = prepareStep;
    }

}