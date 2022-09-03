package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.DestroyNativeEnvStepRunParam;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

@StepRun(
        type = EnumStepRunType.DESTROY_NATIVE_ENV_TYPE,
        name = EnumStepRunType.DESTROY_NATIVE_ENV_NAME,
        paramClass = DestroyNativeEnvStepRunParam.class)
public class DestroyNativeEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(DestroyNativeEnvStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private DestroyNativeEnvStepRunParam param;

    public DestroyNativeEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), DestroyNativeEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        logger.info("destroy native env");

        // remove env dir in workspace
        String envInstanceUuid = param.getEnvInstanceUuid();
        String workingDir = Paths.get(
                agentClientConfig.getDataDir(),
                "workspace",
                envInstanceUuid).toFile().getCanonicalFile().toString();

        boolean removed = FileUtil.rmDir(workingDir);
        if(!removed){
            logger.error("remove env dir [{}] error", workingDir);
        }

        return new Response();
    }

    @Override
    public Response stop() throws Exception {
        throw new NotImplementedException();
    }
}
