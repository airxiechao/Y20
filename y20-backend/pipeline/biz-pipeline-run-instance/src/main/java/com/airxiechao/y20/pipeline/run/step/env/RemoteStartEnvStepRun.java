package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.RemoteStartEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.StartDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.StartNativeEnvStepRunParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.REMOTE_START_ENV_TYPE,
        name = EnumStepRunType.REMOTE_START_ENV_NAME,
        paramClass = RemoteStartEnvStepRunParam.class)
public class RemoteStartEnvStepRun extends RemoteStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(RemoteStartEnvStepRun.class);

    private RemoteStartEnvStepRunParam param;

    public RemoteStartEnvStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteStartEnvStepRunParam.class);

        PipelineStep prepareStep = new PipelineStep();
        if(!StringUtil.isBlank(env.getDockerContainerId())){
            prepareStep.setType(EnumStepRunType.START_DOCKER_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new StartDockerEnvStepRunParam()));
        }else{
            prepareStep.setType(EnumStepRunType.START_NATIVE_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new StartNativeEnvStepRunParam()));
        }

        this.step = prepareStep;
    }

}