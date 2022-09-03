package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.PrepareDockerEnvStepRunParam;
import com.airxiechao.y20.util.DockerUtil;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;

@StepRun(
        type = EnumStepRunType.PREPARE_DOCKER_ENV_TYPE,
        name = EnumStepRunType.PREPARE_DOCKER_ENV_NAME,
        paramClass = PrepareDockerEnvStepRunParam.class)
public class PrepareDockerEnvStepRun extends AbstractPrepareEnvStepRun {

    private static final Logger logger = LoggerFactory.getLogger(PrepareDockerEnvStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private PrepareDockerEnvStepRunParam param;

    public PrepareDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        this.param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), PrepareDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        appendLogLine(String.format("prepare docker env on agent [%s]", agentClientConfig.getAgentId()));

        String envInstanceUuid = getStepRunInstanceUuid();
        String hostWorkingDir = Paths.get(
                agentClientConfig.getDataDir(),
                "workspace",
                envInstanceUuid).toFile().getCanonicalFile().toString();
        String dockerWorkingDir = "/workspace";

        FileUtil.mkDirs(hostWorkingDir);

        String cacheDir = Paths.get(agentClientConfig.getDataDir(), "cache").toFile().getCanonicalFile().toString();
        FileUtil.mkDirs(cacheDir);

        // link cache dirs
        Map<String, String> linkBinds = null;
        if(!StringUtil.isBlank(param.getCacheDirs())){
            linkBinds = linkDockerCacheDirs(param.getCacheDirs(), cacheDir, dockerWorkingDir);
        }

        // sync artifact project file
        if(!StringUtil.isBlank(param.getArtifactProjectFileDir())){
            appendLogLine("sync artifact project file...");
            syncArtifactFile(param.getArtifactProjectFileDir(), hostWorkingDir);
        }

        // sync artifact pipeline file
        if(!StringUtil.isBlank(param.getArtifactPipelineFileDir())){
            appendLogLine("sync artifact pipeline file...");
            syncArtifactFile(param.getArtifactPipelineFileDir(), hostWorkingDir);
        }

        // create docker container
        appendLogLine("create docker container...");
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            try {
                String name = "y20-" + getStepRunInstanceUuid();
                String containerId = DockerUtil.createContainer(
                        client,
                        name,
                        param.getImage(),
                        param.getImageServerAddress(), param.getImageServerUsername(), param.getImageServerPassword(),
                        hostWorkingDir, dockerWorkingDir,
                        linkBinds,
                        param.getEnv(),
                        getPipedStream(),
                        () -> flagStop);

                Env env = new Env();
                env.setDockerHostWorkingDir(hostWorkingDir);
                env.setWorkingDir(dockerWorkingDir);
                env.setDockerContainerId(containerId);
                env.setEnvInstanceUuid(envInstanceUuid);

                return new Response().data(env);
            } catch (Exception e) {
                return new Response().error(e.getMessage());
            }
        }
    }

}
