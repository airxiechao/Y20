package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.StartNativeEnvStepRunParam;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.START_NATIVE_ENV_TYPE,
        name = EnumStepRunType.START_NATIVE_ENV_NAME,
        paramClass = StartNativeEnvStepRunParam.class)
public class StartNativeEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(StartNativeEnvStepRun.class);

    private StartNativeEnvStepRunParam param;

    public StartNativeEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), StartNativeEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        logger.info("start native env");
        return new Response();
    }

    @Override
    public Response stop() throws Exception {
        throw new NotImplementedException();
    }

}