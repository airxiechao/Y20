package com.airxiechao.y20.pipeline.run.step.program;

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
        order = 2,
        type = EnumStepRunType.REMOTE_PROGRAM_TYPE,
        name = EnumStepRunType.REMOTE_PROGRAM_NAME,
        category = EnumStepRunCategory.PROCESS,
        icon = "wysiwyg",
        description = "直接运行程序进程",
        paramClass = RemoteProgramStepRunParam.class
)
public class RemoteProgramStepRun extends RemoteStepRunInstance {

    private RemoteProgramStepRunParam param;

    public RemoteProgramStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteProgramStepRunParam.class);

        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new RuntimeException("no agent");
        }

        PipelineStep programStep = null;
        String agentOs = agentResp.getData().getOs();
        if(StringUtil.isBlank(this.env.getDockerContainerId())){
            // not in docker
            switch (agentOs){
                case OsUtil.OS_WINDOWS:
                    programStep = new PipelineStep();
                    programStep.setType(EnumStepRunType.WINDOWS_PROGRAM_NOT_IN_DOCKER_ENV_TYPE);
                    programStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ProgramNotInDockerEnvStepRunParam(
                            param.getCommand(), env.getWorkingDir())));
                    break;
                case OsUtil.OS_LINUX:
                    programStep = new PipelineStep();
                    programStep.setType(EnumStepRunType.LINUX_PROGRAM_NOT_IN_DOCKER_ENV_TYPE);
                    programStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ProgramNotInDockerEnvStepRunParam(
                            param.getCommand(), env.getWorkingDir())));
                    break;
                default:
                    break;
            }

        }else{
            // in docker
            switch (agentOs) {
                case OsUtil.OS_WINDOWS:
                case OsUtil.OS_LINUX:
                    programStep = new PipelineStep();
                    programStep.setType(EnumStepRunType.PROGRAM_IN_DOCKER_ENV_TYPE);
                    programStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ProgramInDockerEnvStepRunParam(param.getCommand(), env.getWorkingDir())));
                    break;
                default:
                    break;
            }
        }

        if(null == programStep){
            throw new RuntimeException("no supported program step");
        }

        this.step = programStep;
    }

}