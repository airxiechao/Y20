package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.StopDockerEnvStepRunParam;
import com.airxiechao.y20.util.DockerUtil;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@StepRun(
        type = EnumStepRunType.STOP_DOCKER_ENV_TYPE,
        name = EnumStepRunType.STOP_DOCKER_ENV_NAME,
        paramClass = StopDockerEnvStepRunParam.class)
public class StopDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(StopDockerEnvStepRun.class);

    private StopDockerEnvStepRunParam param;

    public StopDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), StopDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        String containerId = this.env.getDockerContainerId();
        logger.info("stop docker env [containerId:{}]", containerId);
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            try {
                DockerUtil.stopContainer(client, containerId);
                return new Response();
            } catch (Exception e) {
                logger.error("stop docker container [{}] error", containerId, e);
                return new Response().error(e.getMessage());
            }
        }
    }

    @Override
    public Response stop() throws Exception {
        throw new NotImplementedException();
    }
}