package com.airxiechao.y20.agent.client.monitor.scheduler.task;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.os.*;
import com.airxiechao.y20.agent.client.scheduler.IMonitorTask;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorAgentStatusParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AgentMonitorTask implements IMonitorTask {

    private static final Logger logger = LoggerFactory.getLogger(AgentMonitorTask.class);

    private String agentId;
    private AgentRestClient agentRestClient;

    public AgentMonitorTask(String agentId, AgentRestClient agentRestClient) {
        this.agentId = agentId;
        this.agentRestClient = agentRestClient;
    }

    @Override
    public void run() throws Exception {
        logger.debug("monitor agent task [agentId:{}] run...", agentId);

        HostMetric hostMetric = HostUtil.getHostMetric();
        List<FileSystemMetric> filesystem = hostMetric.getFilesystem();
        if(null != filesystem){
            filesystem = filesystem.stream()
                    .filter(f -> {
                        if(f.getTotalBytes() <= 0){
                            return false;
                        }

                        if(f.getMount().startsWith("/sys") ||
                                f.getMount().startsWith("/run") ||
                                f.getMount().startsWith("/dev") ||
                                f.getMount().startsWith("/proc") ||
                                f.getMount().startsWith("/tmp") ||
                                f.getMount().startsWith("/var/tmp") ||
                                f.getMount().endsWith("(tmpfs)")
                        ){
                            return false;
                        }

                        return true;
                    })
                    .collect(Collectors.toList());
            hostMetric.setFilesystem(filesystem);
        }

        Response resp = agentRestClient.get(IAgentMonitorRest.class).updateAgentStatus(
                new UpdateMonitorAgentStatusParam(null, agentId,
                        null != hostMetric && null != hostMetric.getCpu() ? hostMetric.getCpu().getLoad() : null,
                        null != hostMetric && null != hostMetric.getMemory() ? hostMetric.getMemory().getAvailableBytes() : null,
                        null != hostMetric && null != hostMetric.getMemory() ? hostMetric.getMemory().getTotalBytes() : null,
                        null != hostMetric ? hostMetric.getFilesystem() : null,
                        null != hostMetric ? hostMetric.getTimestamp() : null
                ));
        if(!resp.isSuccess()){
            logger.error("update monitor agent [{}] status error: {}", agentId, resp.getMessage());
            return;
        }

        logger.debug("update monitor agent [{}] status success", agentId);
    }
}
