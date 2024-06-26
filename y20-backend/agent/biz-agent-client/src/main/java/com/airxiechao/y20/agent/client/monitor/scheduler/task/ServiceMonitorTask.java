package com.airxiechao.y20.agent.client.monitor.scheduler.task;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.os.*;
import com.airxiechao.y20.agent.client.scheduler.IMonitorTask;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.monitor.pojo.ServiceMonitorTarget;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorStatus;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorType;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorStatusParam;
import com.airxiechao.y20.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServiceMonitorTask implements IMonitorTask {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMonitorTask.class);

    private Long projectId;
    private Long monitorId;
    private ServiceMonitorTarget target;
    private AgentRestClient agentRestClient;

    public ServiceMonitorTask(Long projectId, Long monitorId, ServiceMonitorTarget target, AgentRestClient agentRestClient) {
        this.projectId = projectId;
        this.monitorId = monitorId;
        this.target = target;
        this.agentRestClient = agentRestClient;
    }

    @Override
    public void run() throws Exception {
        logger.debug("monitor task [monitorId:{}, type:{}, target:{}] run...", monitorId, EnumMonitorType.SERVICE, target.getNamePattern());

        List<ServiceInfo> serviceInfoList;
        switch(OsUtil.getOs()){
            case OsUtil.OS_WINDOWS:
                serviceInfoList = WindowsServiceUtil.listService();
                break;
            case OsUtil.OS_LINUX:
                serviceInfoList = LinuxServiceUtil.listService();
                break;
            default:
                throw new Exception("os not support");
        }

        String name = target.getNamePattern();
        boolean isAlive = false;
        Long pid = null;
        for (ServiceInfo serviceInfo : serviceInfoList) {
            String serviceName = serviceInfo.getName();
            if(serviceName.matches(name)){
                isAlive = true;
                switch(OsUtil.getOs()){
                    case OsUtil.OS_WINDOWS:
                        pid = WindowsServiceUtil.getServicePid(serviceName);
                        break;
                    case OsUtil.OS_LINUX:
                        pid = LinuxServiceUtil.getServicePid(serviceName);
                        break;
                    default:
                        throw new Exception("os not support");
                }
                break;
            }
        }

        ProcessMetric processMetric = null;
        if(isAlive && null != pid){
            switch(OsUtil.getOs()){
                case OsUtil.OS_WINDOWS:
                    processMetric = WindowsProcessUtil.getProcessMetric(pid);
                    break;
                case OsUtil.OS_LINUX:
                    processMetric = LinuxProcessUtil.getProcessMetric(pid);
                    break;
                default:
                    throw new Exception("os not support");
            }
        }

        Response resp = agentRestClient.get(IAgentMonitorRest.class).updateStatus(
                new UpdateMonitorStatusParam(null, projectId, monitorId, isAlive ? EnumMonitorStatus.OK : EnumMonitorStatus.ERROR,
                        null != processMetric ? processMetric.getCpuUsage() : null,
                        null != processMetric ? processMetric.getMemoryBytes() : null,
                        null != processMetric ? processMetric.getTimestamp() : null
                ));
        if(!resp.isSuccess()){
            logger.error("update monitor [{}] status error: {}", monitorId, resp.getMessage());
            return;
        }

        logger.debug("update monitor [{}] status to [{}]", monitorId, isAlive);
    }
}
