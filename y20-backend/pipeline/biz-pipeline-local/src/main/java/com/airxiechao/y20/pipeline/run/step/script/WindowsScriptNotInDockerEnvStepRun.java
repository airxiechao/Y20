package com.airxiechao.y20.pipeline.run.step.script;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.CharsetUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.WindowsScriptNotInDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.ProcessUtil;
import com.airxiechao.y20.util.WindowsUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@StepRun(
        type = EnumStepRunType.WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_TYPE,
        name = EnumStepRunType.WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_NAME,
        paramClass = WindowsScriptNotInDockerEnvStepRunParam.class)
public class WindowsScriptNotInDockerEnvStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(WindowsScriptNotInDockerEnvStepRun.class);

    private WindowsScriptNotInDockerEnvStepRunParam param;
    private Process process;
    private boolean flagStop = false;

    public WindowsScriptNotInDockerEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), WindowsScriptNotInDockerEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {

        // read env variables from file (.env)
        Map<String, String> envVariables = LocalStepRunUtil.readEnvVariablesFromFile(param.getWorkingDir());

        // merge all script
        StringBuilder scriptSb = new StringBuilder();

        String executor = param.getExecutor();
        String executorType = WindowsUtil.getExecutorType(executor);
        switch (executorType){
            case WindowsUtil.EXECUTOR_CMD:
                // cmd
                envVariables.put("PROMPT", "> ");
                scriptSb.append(param.getScript());
                this.process = WindowsUtil.executeScriptAsync(scriptSb.toString(), executor, param.getWorkingDir(), envVariables);
                break;
            case WindowsUtil.EXECUTOR_POWERSHELL:
                // cmd
                scriptSb.append(WindowsUtil.buildHidePowershellPromptScript());
                scriptSb.append(param.getScript());
                this.process = WindowsUtil.executeScriptAsync(scriptSb.toString(), executor, param.getWorkingDir(), envVariables);
                break;
            default:
                scriptSb.append(param.getScript());
                this.process = WindowsUtil.executeScriptAsync(scriptSb.toString(), executor, param.getWorkingDir(), envVariables);
                break;
        }

        // check last exit code
        WindowsUtil.checkLastExitCode(this.process, executorType);

        // export output variables
        String outputs = param.getOutputs();
        if(!StringUtil.isBlank(outputs)){
            String[] names = outputs.split(",");
            WindowsUtil.exportEnvVariablesToFile(this.process, param.getWorkingDir(), names, executorType);
        }

        // exit
        ProcessUtil.writeWindowsCommandString(this.process, "exit");

        // read output
        AtomicInteger numExitedFlush = new AtomicInteger(2);
        InputStream processInputStream = this.process.getInputStream();
        StreamUtil.readStringInputStreamNoneBlocking(processInputStream, 200, StandardCharsets.UTF_8, (log)->{
            if(!StringUtil.isEmpty(log)){
                appendLog(log);
            }else{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }

            if(flagStop){
                return false;
            }

            if(numExitedFlush.get() <= 0){
                return false;
            }

            if(!this.process.isAlive()){
                if(StringUtil.isBlank(log)){
                    numExitedFlush.getAndAdd(-1);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }

            return true;
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
            logger.info("destroy process [{}]", this.process.pid());
            this.process.destroy();
        }
        return new Response();
    }

}