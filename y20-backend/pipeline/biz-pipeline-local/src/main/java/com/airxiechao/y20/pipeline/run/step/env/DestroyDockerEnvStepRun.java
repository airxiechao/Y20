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
import com.airxiechao.y20.pipeline.run.step.param.DestroyDockerEnvStepRunParam;
import com.airxiechao.y20.util.DockerUtil;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

@StepRun(
        type = EnumStepRunType.DESTROY_DOCKER_ENV_TYPE,
        name = EnumStepRunType.DESTROY_DOCKER_ENV_NAME,
        paramClass = DestroyDockerEnvStepRunParam.class)
public class DestroyDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(DestroyDockerEnvStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private DestroyDockerEnvStepRunParam param;

    public DestroyDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), DestroyDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        String containerId = this.env.getDockerContainerId();
        logger.info("destroy docker env [containerId:{}]", containerId);

        if(!StringUtil.isNullOrEmpty(containerId)){
            try(DockerClient client = DefaultDockerClient.fromEnv().build()){
                DockerUtil.removeContainer(client, containerId);
            }catch (Exception e){
                logger.error("remove docker container [{}] error", containerId, e);
                return new Response().error(e.getMessage());
            }
        }

        // remove env dir in workspace
        String envInstanceUuid = param.getEnvInstanceUuid();
        String hostWorkingDir = Paths.get(
                agentClientConfig.getDataDir(),
                "workspace",
                envInstanceUuid).toFile().getCanonicalFile().toString();

        boolean removed = FileUtil.rmDir(hostWorkingDir);
        if(!removed){
            logger.error("remove env dir [{}] error", hostWorkingDir);
        }

        return new Response();
    }

    @Override
    public Response stop() throws Exception {
        throw new NotImplementedException();
    }
}
