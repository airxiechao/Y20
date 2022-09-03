package com.airxiechao.y20.pipeline.run.step.program;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.ProgramNotInDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.ProcessUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.airxiechao.y20.util.ProcessUtil.splitCommandString;

@StepRun(
        type = EnumStepRunType.LINUX_PROGRAM_NOT_IN_DOCKER_ENV_TYPE,
        name = EnumStepRunType.LINUX_PROGRAM_NOT_IN_DOCKER_ENV_NAME,
        paramClass = ProgramNotInDockerEnvStepRunParam.class)
public class LinuxProgramNotInDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(LinuxProgramNotInDockerEnvStepRun.class);

    private ProgramNotInDockerEnvStepRunParam param;
    private Process process;
    private boolean flagStop = false;

    public LinuxProgramNotInDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), ProgramNotInDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {

        StreamUtil.PipedStream logStream = getPipedStream();

        // read env variables from file (.env)
        Map<String, String> envVariables = LocalStepRunUtil.readEnvVariablesFromFile(param.getWorkingDir());

        // start process
        this.process = ProcessUtil.startPtyProcess(
                splitCommandString(param.getCommand()),
                param.getWorkingDir(),
                envVariables);

        // read output
        InputStream processInputStream = this.process.getInputStream();
        StreamUtil.readStringInputStream(processInputStream, 200, StandardCharsets.UTF_8, (log)->{
            if(!StringUtil.isEmpty(log)){
                appendLog(log);
            }
        });

        Long exitCode = Long.valueOf(this.process.waitFor());

        // read output variables
        Map<String, String> outputVariables = LocalStepRunUtil.readOutputVariablesFromFile(param.getWorkingDir());

        if(null != outputVariables && outputVariables.size() > 0) {
            appendLogLine("export output variables: " + JSON.toJSONString(outputVariables));
        }

        if(0 != exitCode){
            return new Response().error("exit with " + exitCode);
        }

        return new Response().data(outputVariables);
    }

    @Override
    public Response stop() throws Exception {
        this.flagStop = true;

        if(null != this.process){
            this.process.destroy();
        }
        return new Response();
    }

}