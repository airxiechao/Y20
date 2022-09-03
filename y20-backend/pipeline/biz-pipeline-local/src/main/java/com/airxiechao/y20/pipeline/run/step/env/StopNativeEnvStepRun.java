package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.StopNativeEnvStepRunParam;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.STOP_NATIVE_ENV_TYPE,
        name = EnumStepRunType.STOP_NATIVE_ENV_NAME,
        paramClass = StopNativeEnvStepRunParam.class)
public class StopNativeEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(StopNativeEnvStepRun.class);

    private StopNativeEnvStepRunParam param;

    public StopNativeEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), StopNativeEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        logger.info("stop native env");
        return new Response();
    }

    @Override
    public Response stop() throws Exception {
        throw new NotImplementedException();
    }
}