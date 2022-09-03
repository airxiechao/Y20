package com.airxiechao.y20.pipeline.run.step.script;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.ScriptInDockerProcessStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.DockerUtil;
import com.airxiechao.y20.util.LinuxUtil;
import com.airxiechao.y20.util.ProcessUtil;
import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@StepRun(
        type = EnumStepRunType.SCRIPT_IN_DOCKER_PROCESS_TYPE,
        name = EnumStepRunType.SCRIPT_IN_DOCKER_PROCESS_NAME,
        paramClass = ScriptInDockerProcessStepRunParam.class)
public class ScriptInDockerProcessStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(ScriptInDockerProcessStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private ScriptInDockerProcessStepRunParam param;
    private Process process;
    private boolean flagStop = false;

    public ScriptInDockerProcessStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), ScriptInDockerProcessStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        String executor = param.getExecutor();
        if(StringUtil.isBlank(executor)){
            executor = "bash";
        }
        String executorType = LinuxUtil.getExecutorType(executor);
        this.process = ProcessUtil.startPtyProcess(
                new String[]{
                        "docker",
                        "exec",
                        "-w",
                        param.getWorkingDir(),
                        "-it",
                        this.env.getDockerContainerId(),
                        executor
                },
                param.getWorkingDir(),
                null);

        int waitSecs = 1;
        appendLogLine(String.format("waiting %ds for process start...", waitSecs));
        Thread.sleep(waitSecs * 1000);

        // merge all script
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // init script
        switch (executorType){
            case LinuxUtil.EXECUTOR_BASH:
            case LinuxUtil.EXECUTOR_SH:
                LinuxUtil.hidePrompt(byteArrayOutputStream);
                break;
            default:
                break;
        }

        // script
        ProcessUtil.writeLinuxCommandString(byteArrayOutputStream, param.getScript());

        // export output variables
        String outputs = param.getOutputs();
        if(!StringUtil.isBlank(outputs)){
            // check last exit code
            LinuxUtil.checkLastExitCode(byteArrayOutputStream);

            String[] names = outputs.split(",");
            LinuxUtil.exportEnvVariablesToFile(byteArrayOutputStream, param.getWorkingDir(), names);
        }

        // exit
        ProcessUtil.writeLinuxCommandString(byteArrayOutputStream, "exit");

        ProcessUtil.writeBytes(this.process, byteArrayOutputStream.toByteArray());

        // read output
        InputStream processInputStream = this.process.getInputStream();
        StreamUtil.readStringInputStream(processInputStream, 200, StandardCharsets.UTF_8, (log)->{
            if(!StringUtil.isEmpty(log)){
                appendLog(log);
            }
        });

        // read output variables
        String hostWorkingDir = null;
        try(DockerClient client = DefaultDockerClient.fromEnv().build()){
            ContainerInfo containerInfo = DockerUtil.inspectContainer(client, this.env.getDockerContainerId());
            for (String bind : containerInfo.hostConfig().binds()) {
                if(bind.endsWith(":/workspace")){
                    hostWorkingDir = bind.substring(0, bind.lastIndexOf(":/workspace"));
                    break;
                }
            }
        }
        Map<String, String> outputVariables = null;
        if(null != hostWorkingDir){
            outputVariables = LocalStepRunUtil.readOutputVariablesFromFile(hostWorkingDir);
            if(null != outputVariables && outputVariables.size() > 0) {
                appendLogLine("export output variables: " + JSON.toJSONString(outputVariables));
            }
        }

        Long exitCode = Long.valueOf(this.process.waitFor());

        if(0 != exitCode){
            return new Response().error("exit with " + exitCode);
        }

        return new Response().data(outputVariables);
    }

    @Override
    public Response stop() throws Exception {
        this.flagStop = true;
        if(null != this.process){
            ProcessUtil.writeLinuxCommandByte(this.process, 3);
            ProcessUtil.writeLinuxCommandString(this.process, "exit");
            this.process.destroy();
        }

        return new Response();
    }
}