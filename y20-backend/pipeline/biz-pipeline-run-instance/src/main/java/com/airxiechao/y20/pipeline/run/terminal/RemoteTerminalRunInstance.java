package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.agent.pojo.Agent;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.common.core.rest.ServiceCallAgentClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.rpc.api.ILocalTerminalRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.terminal.CreateTerminalRunRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.terminal.DestroyTerminalRunRpcParam;
import com.airxiechao.y20.pipeline.rpc.param.terminal.PushTerminalRunInputRpcParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class RemoteTerminalRunInstance implements ITerminalRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTerminalRunInstance.class);

    protected String terminalRunInstanceUuid;

    private String pipelineRunInstanceUuid;
    private String terminalType;
    private Env env;

    private StreamUtil.PipedStream pipedStream = new StreamUtil.PipedStream();

    public RemoteTerminalRunInstance(String pipelineRunInstanceUuid, String terminalType, Env env){
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.terminalType = terminalType;
        this.env = env;
    }

    @Override
    public void create() throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response resp = ServiceCallAgentClient.get(ILocalTerminalRunRpc.class, clientId).createTerminalRun(
                new CreateTerminalRunRpcParam(pipelineRunInstanceUuid, env.getDockerContainerId(), terminalType, env.getWorkingDir()));

        if(resp.isSuccess()){
            this.terminalRunInstanceUuid = (String)resp.getData();

            // consume output
//            InputStream inputStream = this.pipedStream.getInputStream();
//            CompletableFuture.runAsync(() -> {
//                try {
//                    StreamUtil.readStringInputStream(inputStream, 10, Charset.forName("UTF-8"), outputConsumer);
//                } catch (Exception e) {
//
//                }
//            });
        } else {
            throw new Exception("create terminal error");
        }
    }

    @Override
    public void destroy() throws Exception {
        if(null == terminalRunInstanceUuid){
            throw new Exception("no terminal");
        }
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response resp = ServiceCallAgentClient.get(ILocalTerminalRunRpc.class,clientId).destroyTerminalRun(
                new DestroyTerminalRunRpcParam(terminalRunInstanceUuid));
        if(!resp.isSuccess()){
            throw new Exception("destroy terminal error");
        }
    }

    @Override
    public void input(String text) throws Exception {
        if(null == terminalRunInstanceUuid){
            throw new Exception("no terminal");
        }
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response resp = ServiceCallAgentClient.get(ILocalTerminalRunRpc.class, clientId).pushTerminalRunInput(
                new PushTerminalRunInputRpcParam(terminalRunInstanceUuid, text));
        if(!resp.isSuccess()){
            throw new Exception("destroy terminal error");
        }
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream(){
        return this.pipedStream.getOutputStream();
    }

    @Override
    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }
}
