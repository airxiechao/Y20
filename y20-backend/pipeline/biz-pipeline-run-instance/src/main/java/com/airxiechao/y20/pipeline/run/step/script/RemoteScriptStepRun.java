package com.airxiechao.y20.pipeline.run.step.script;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunCategory;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.*;
import com.airxiechao.y20.util.OsUtil;

@StepRun(
        visible = true,
        order = 1,
        type = EnumStepRunType.REMOTE_SCRIPT_TYPE,
        name = EnumStepRunType.REMOTE_SCRIPT_NAME,
        category = EnumStepRunCategory.PROCESS,
        icon = "description",
        description = "打开终端或解释程序，执行脚本。比如Windows的cmd脚本或powershell脚本，Linux的bash脚本",
        paramClass = RemoteScriptStepRunParam.class,
        outputs = {"\"输出变量\"参数中的变量"}
)
public class RemoteScriptStepRun extends RemoteStepRunInstance {

    private RemoteScriptStepRunParam param;

    public RemoteScriptStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteScriptStepRunParam.class);

        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new RuntimeException("no agent");
        }

        PipelineStep scriptStep = null;
        String agentOs = agentResp.getData().getOs();
        if(StringUtil.isBlank(this.env.getDockerContainerId())){
            // not in docker
            switch (agentOs){
                case OsUtil.OS_WINDOWS:
                    scriptStep = new PipelineStep();
                    scriptStep.setType(EnumStepRunType.WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new WindowsScriptNotInDockerEnvStepRunParam(
                            param.getScript(), param.getExecutor(), env.getWorkingDir(), param.getOutputs())));
                    break;
                case OsUtil.OS_LINUX:
                    scriptStep = new PipelineStep();
                    scriptStep.setType(EnumStepRunType.LINUX_SCRIPT_NOT_IN_DOCKER_ENV_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new LinuxScriptNotInDockerEnvStepRunParam(
                            param.getScript(), param.getExecutor(), env.getWorkingDir(), param.getOutputs())));
                    break;
                default:
                    break;
            }
        }else{
            // in docker
            switch (agentOs){
                case OsUtil.OS_WINDOWS:
                case OsUtil.OS_LINUX:
                    scriptStep = new PipelineStep();
                    //scriptStep.setType(EnumStepRunType.SCRIPT_IN_DOCKER_ENV_TYPE);
                    //scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ScriptInDockerEnvStepRunParam(param.getScript(), param.getExecutor(), param.getOutputs())));
                    scriptStep.setType(EnumStepRunType.SCRIPT_IN_DOCKER_PROCESS_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ScriptInDockerProcessStepRunParam(param.getScript(), param.getExecutor(), env.getWorkingDir(), param.getOutputs())));
                    break;
                default:
                    break;
            }
        }

        if(null == scriptStep){
            throw new RuntimeException("no supported script step");
        }

        this.step = scriptStep;
    }

}