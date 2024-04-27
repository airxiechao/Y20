package com.airxiechao.y20.agent.client.monitor.scheduler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.agent.client.monitor.scheduler.task.AgentMonitorTask;
import com.airxiechao.y20.agent.client.monitor.scheduler.task.ProcessMonitorTask;
import com.airxiechao.y20.agent.client.monitor.scheduler.task.ServiceMonitorTask;
import com.airxiechao.y20.agent.client.scheduler.IMonitorTask;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.common.core.global.Global;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.monitor.pojo.Monitor;
import com.airxiechao.y20.monitor.pojo.ServiceMonitorTarget;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorType;
import com.airxiechao.y20.monitor.pojo.ProcessMonitorTarget;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.ListMonitorParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MonitorJob implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(MonitorJob.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private IAgentRpcClient agentRpcClient = Global.get(IAgentRpcClient.class);

    @Override
    public void run() {
        logger.debug("monitor job run");

        agentRpcClient.getAllChannelHandlerContext().forEach(ctx -> {
            List<IMonitorTask> tasks = new ArrayList<>();

            // 查询监视
            AgentRestClient agentRestClient = new AgentRestClient(
                    agentRpcClient, ctx,
                    agentClientConfig.getServerHost(),
                    agentClientConfig.getServerRestPort(), 
                    agentClientConfig.isServerRestUseSsl(),
                    agentClientConfig.getAccessToken());

            Response<List<Monitor>> monitorResp = agentRestClient.get(IAgentMonitorRest.class)
                    .list(new ListMonitorParam(null, null, agentClientConfig.getAgentId(), null, null, null, null, null));

            if(monitorResp.isSuccess()){
                List<Monitor> monitors = monitorResp.getData();

                // 构造监视任务
                monitors.forEach(monitor -> {
                    switch (monitor.getType()){
                        case EnumMonitorType.PROCESS:
                            ProcessMonitorTarget processMonitorTarget = JSON.toJavaObject((JSONObject)monitor.getTarget(), ProcessMonitorTarget.class);
                            tasks.add(new ProcessMonitorTask(monitor.getProjectId(), monitor.getMonitorId(), processMonitorTarget, agentRestClient));
                            break;
                        case EnumMonitorType.SERVICE:
                            ServiceMonitorTarget serviceMonitorTarget = JSON.toJavaObject((JSONObject)monitor.getTarget(), ServiceMonitorTarget.class);
                            tasks.add(new ServiceMonitorTask(monitor.getProjectId(), monitor.getMonitorId(), serviceMonitorTarget, agentRestClient));
                            break;
                    }
                });
            } else {
                logger.error("list monitor error: {}", monitorResp.getMessage());
            }

            // 构造节点监视任务
            tasks.add(new AgentMonitorTask(agentClientConfig.getAgentId(), agentRestClient));

            // 执行监视任务
            tasks.forEach(task -> {
                try {
                    task.run();
                } catch (Exception e) {
                    logger.error("monitor task error", e);
                }
            });
        });


    }
}
