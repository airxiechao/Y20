package com.airxiechao.y20.pipeline.run.step.script;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.ScriptInDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.DockerUtil;
import com.airxiechao.y20.util.LinuxUtil;
import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@StepRun(
        type = EnumStepRunType.SCRIPT_IN_DOCKER_ENV_TYPE,
        name = EnumStepRunType.SCRIPT_IN_DOCKER_ENV_NAME,
        paramClass = ScriptInDockerEnvStepRunParam.class)
public class ScriptInDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(ScriptInDockerEnvStepRun.class);

    private ScriptInDockerEnvStepRunParam param;

    public ScriptInDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), ScriptInDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        StreamUtil.PipedStream logStream = getPipedStream();
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){

            String executor = param.getExecutor();
            if(StringUtil.isBlank(executor)){
                executor = "bash";
            }
            String executorType = LinuxUtil.getExecutorType(executor);

            // merge all script
            StringBuilder scriptSb = new StringBuilder();

            // init script
            switch (executorType){
                case LinuxUtil.EXECUTOR_BASH:
                case LinuxUtil.EXECUTOR_SH:
                    scriptSb.append(LinuxUtil.buildHidePromptScript());
                    break;
                default:
                    break;
            }

            // script
            scriptSb.append(param.getScript());

            // export output variables script
            String outputs = param.getOutputs();
            if(!StringUtil.isBlank(outputs)){
                scriptSb.append("\n");

                // check last exit code script
                scriptSb.append(LinuxUtil.buildCheckLastExitCodeScript());

                String[] variables = outputs.split(",");
                scriptSb.append(LinuxUtil.buildExportEnvVariablesToFileScript("/workspace", variables));
            }

            // execute script
            String script = scriptSb.toString();
            Long exitCode =  DockerUtil.executeCommand(client, this.env.getDockerContainerId(), script, executor, logStream);

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
        if(!StringUtil.isBlank(this.env.getDockerContainerId())){
            try(DockerClient client = DefaultDockerClient.fromEnv().build()){
                DockerUtil.stopContainer(client, this.env.getDockerContainerId());
                DockerUtil.removeContainer(client, this.env.getDockerContainerId());
            }catch (Exception e){
                return new Response().error(e.getMessage());
            }
        }

        return new Response();
    }
}
