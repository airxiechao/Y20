package com.airxiechao.y20.pipeline.run.step.program;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.ProgramInDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.DockerUtil;
import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerInfo;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@StepRun(
        type = EnumStepRunType.PROGRAM_IN_DOCKER_ENV_TYPE,
        name = EnumStepRunType.PROGRAM_IN_DOCKER_ENV_NAME,
        paramClass = ProgramInDockerEnvStepRunParam.class)
public class ProgramInDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(ProgramInDockerEnvStepRun.class);

    private ProgramInDockerEnvStepRunParam param;

    public ProgramInDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), ProgramInDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {

        StreamUtil.PipedStream logStream = getPipedStream();

        // start process
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            Long exitCode = DockerUtil.executeCommand(client, this.env.getDockerContainerId(), param.getCommand(), null, logStream);

            // read output variables
            String hostWorkingDir = null;
            ContainerInfo containerInfo = DockerUtil.inspectContainer(client, this.env.getDockerContainerId());
            for (String bind : containerInfo.hostConfig().binds()) {
                if(bind.endsWith(":/workspace")){
                    hostWorkingDir = bind.substring(0, bind.lastIndexOf(":/workspace"));
                    break;
                }
            }

            Map<String, String> outputVariables = null;
            if(null != hostWorkingDir){
                outputVariables = LocalStepRunUtil.readOutputVariablesFromFile(hostWorkingDir);
                if(null != outputVariables && outputVariables.size() > 0) {
                    appendLogLine("export output variables: " + JSON.toJSONString(outputVariables));
                }
            }

            if(0 != exitCode){
                return new Response().error("exit with " + exitCode);
            }

            return new Response().data(outputVariables);
        }
    }

    @Override
    public Response stop() throws Exception {
        if(!StringUtil.isNullOrEmpty(this.env.getDockerContainerId())){
            try(DockerClient client = DefaultDockerClient.fromEnv().build()){
                DockerUtil.stopContainer(client, this.env.getDockerContainerId());
                DockerUtil.removeContainer(client, this.env.getDockerContainerId());
            }catch (Exception e){
                logger.error("stop and destroy docker env error", e);
                return new Response().error(e.getMessage());
            }
        }

        return new Response();
    }

}
