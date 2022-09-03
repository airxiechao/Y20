package com.airxiechao.y20.pipeline.run.step.plugin;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunCategory;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.LinuxScriptNotInDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemotePluginTencentCosCmdStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.ScriptInDockerProcessStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.WindowsScriptNotInDockerEnvStepRunParam;
import com.airxiechao.y20.util.OsUtil;

@StepRun(
        visible = true,
        order = 1,
        type = EnumStepRunType.REMOTE_PLUGIN_TENCENT_COSCMD_TYPE,
        name = EnumStepRunType.REMOTE_PLUGIN_TENCENT_COSCMD_NAME,
        category = EnumStepRunCategory.TENCENT_COS,
        icon = "storage",
        description = "使用 COSCMD 工具，实现文件的上传、下载、删除等操作",
        requires = {
                "Python 2.7/3.5/3.6/3.9",
                "最新版本的 pip"
        },
        paramClass = RemotePluginTencentCosCmdStepRunParam.class)
public class RemotePluginTencentCosCmdStepRun extends RemoteStepRunInstance {

    private RemotePluginTencentCosCmdStepRunParam param;

    public RemotePluginTencentCosCmdStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemotePluginTencentCosCmdStepRunParam.class);

        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new RuntimeException("no agent");
        }

        String agentOs = agentResp.getData().getOs();
        String script = buildScript(param.getScript(), agentOs);

        PipelineStep scriptStep = new PipelineStep();
        if(StringUtil.isBlank(this.env.getDockerContainerId())){
            // not in docker
            switch (agentOs){
                case OsUtil.OS_WINDOWS:
                    scriptStep = new PipelineStep();
                    scriptStep.setType(EnumStepRunType.WINDOWS_SCRIPT_NOT_IN_DOCKER_ENV_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new WindowsScriptNotInDockerEnvStepRunParam(
                            script, null, env.getWorkingDir(), null)));
                    break;
                case OsUtil.OS_LINUX:
                    scriptStep = new PipelineStep();
                    scriptStep.setType(EnumStepRunType.LINUX_SCRIPT_NOT_IN_DOCKER_ENV_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new LinuxScriptNotInDockerEnvStepRunParam(
                            script, null, env.getWorkingDir(),null)));
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
                    //scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ScriptInDockerEnvStepRunParam(script, param.getExecutor(), param.getOutputs())));
                    scriptStep.setType(EnumStepRunType.SCRIPT_IN_DOCKER_PROCESS_TYPE);
                    scriptStep.setParams(ModelUtil.toMapAndCheckRequiredField(new ScriptInDockerProcessStepRunParam(script, null, env.getWorkingDir(), null)));
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

    private String buildScript(String script, String agentOs){
        String lineSeparator = OsUtil.OS_WINDOWS.equals(agentOs) ? "\r\n" : "\n";
        StringBuilder sb = new StringBuilder();

        if(param.getFlagInstall()){
            sb.append("pip install coscmd");
            sb.append(lineSeparator);

            sb.append(String.format("coscmd config -a %s -s %s -b %s -r %s",
                    param.getSecretId(), param.getSecretKey(), param.getBucket(), param.getRegion()));
            sb.append(lineSeparator);
        }

        sb.append(script);

        return sb.toString();
    }
}
