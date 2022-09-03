package com.airxiechao.y20.pipeline.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.biz.api.IPipelineRunInstanceBiz;
import com.airxiechao.y20.pipeline.biz.api.ITerminalRunInstanceBiz;
import com.airxiechao.y20.pipeline.pojo.constant.EnumTerminalType;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;
import com.airxiechao.y20.pipeline.run.terminal.ITerminalRunInstance;
import com.airxiechao.y20.util.OsUtil;

public class TerminalRunInstanceBizProcess implements ITerminalRunInstanceBiz {

    private IPipelineRunInstanceBiz pipelineRunInstanceBiz = Biz.get(IPipelineRunInstanceBiz.class);

    @Override
    public ITerminalRunInstance createTerminalRunInstance(String pipelineRunInstanceUuid) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        Env currentEnv = pipelineRunInstance.getCurrentEnv();
        if(null == currentEnv){
            throw new Exception("no env");
        }

        Long userId = currentEnv.getUserId();
        String agentId = currentEnv.getAgentId();
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(userId, agentId));
        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String agentOs = agentResp.getData().getOs();
        String terminalType = null;
        if(StringUtil.isBlank(currentEnv.getDockerContainerId())){
            switch (agentOs){
                case OsUtil.OS_LINUX:
                    terminalType = EnumTerminalType.TERMINAL_BASH;
                    break;
                case OsUtil.OS_WINDOWS:
                    terminalType = EnumTerminalType.TERMINAL_CMD;
                    break;
            }
        }else{
            terminalType = EnumTerminalType.TERMINAL_DOCKER;
        }
        if(null == terminalType){
            throw new Exception("no terminal type");
        }

        ITerminalRunInstance terminalRunInstance = pipelineRunInstance.createTerminalRunInstance(terminalType);

        return terminalRunInstance;
    }

    @Override
    public void destroyTerminalRunInstance(String pipelineRunInstanceUuid, String terminalRunInstanceUuid) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        ITerminalRunInstance terminalRunInstance = pipelineRunInstance.getTerminalRunInstance(terminalRunInstanceUuid);
        if(null == terminalRunInstance){
            throw new Exception("no terminal run instance");
        }

        terminalRunInstance.destroy();
    }

    @Override
    public void inputTerminalRunInstance(String pipelineRunInstanceUuid, String terminalRunInstanceUuid, String message) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        ITerminalRunInstance terminalRunInstance = pipelineRunInstance.getTerminalRunInstance(terminalRunInstanceUuid);
        if(null == terminalRunInstance){
            throw new Exception("no terminal run instance");
        }

        terminalRunInstance.input(message);
    }
}
