package com.airxiechao.y20.pipeline.run.step;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.common.core.rest.ServiceCallAgentClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.rpc.api.ILocalStepRpc;
import com.airxiechao.y20.pipeline.rpc.param.step.RunStepRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.step.StopStepRpcParam;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;

public class RemoteStepRunInstance extends AbstractStepRunInstance {

    protected PipelineStep step;
    protected AbstractPipelineRunContext pipelineRunContext;

    public RemoteStepRunInstance(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext){
        super(stepRunInstanceUuid, env);
        this.pipelineRunContext = pipelineRunContext;
    }

    @Override
    public void assemble(PipelineStep step) {
        this.step = step;
    }

    @Override
    public Response run() throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        AgentVo agentVo = agentResp.getData();
        if(!agentVo.getActive()){
            throw new Exception("agent is not active");
        }

        String clientId = agentVo.getClientId();
        if(StringUtil.isBlank(clientId)){
            throw new Exception("no agent client");
        }

        Response resp = ServiceCallAgentClient.get(ILocalStepRpc.class, clientId).runStep(
                        new RunStepRpcParam(step, stepRunInstanceUuid, env));

        if(!resp.isSuccess()){
            throw new Exception(resp.getMessage());
        }

        return resp;
    }

    @Override
    public Response stop() throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        AgentVo agentVo = agentResp.getData();
        if(!agentVo.getActive()){
            throw new Exception("agent is not active");
        }

        String clientId = agentResp.getData().getClientId();
        if(StringUtil.isBlank(clientId)){
            throw new Exception("no agent client");
        }

        return ServiceCallAgentClient.get(ILocalStepRpc.class, clientId).stopStep(new StopStepRpcParam(stepRunInstanceUuid));
    }

    @Override
    public String getType(){
        return step.getType();
    }

    public PipelineStep getStep() {
        return step;
    }
}
