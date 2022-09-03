package com.airxiechao.y20.monitor.scheduler.task;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.util.os.LinuxProcessUtil;
import com.airxiechao.axcboot.util.os.ProcessInfo;
import com.airxiechao.axcboot.util.os.WindowsProcessUtil;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorStatus;
import com.airxiechao.y20.monitor.pojo.constant.EnumMonitorType;
import com.airxiechao.y20.monitor.pojo.ProcessMonitorTarget;
import com.airxiechao.y20.monitor.rest.api.IAgentMonitorRest;
import com.airxiechao.y20.monitor.rest.param.UpdateMonitorStatusParam;
import com.airxiechao.y20.monitor.scheduler.IMonitorTask;
import com.airxiechao.y20.util.OsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProcessMonitorTask implements IMonitorTask {

    private static final Logger logger = LoggerFactory.getLogger(ProcessMonitorTask.class);

    private Long projectId;
    private Long monitorId;
    private ProcessMonitorTarget target;
    private AgentRestClient agentRestClient;

    public ProcessMonitorTask(Long projectId, Long monitorId, ProcessMonitorTarget target, AgentRestClient agentRestClient) {
        this.projectId = projectId;
        this.monitorId = monitorId;
        this.target = target;
        this.agentRestClient = agentRestClient;
    }

    @Override
    public void run() throws Exception {
        logger.debug("monitor task [monitorId:{}, type:{}, target:{}] run...", monitorId, EnumMonitorType.PROCESS, target.getCommandPattern());

        List<ProcessInfo> processInfoList;
        switch(OsUtil.getOs()){
            case OsUtil.OS_WINDOWS:
                processInfoList = WindowsProcessUtil.listProcess();
                break;
            case OsUtil.OS_LINUX:
                processInfoList = LinuxProcessUtil.listProcess();
                break;
            default:
                throw new Exception("os not support");
        }

        String command = target.getCommandPattern();
        boolean isAlive = false;
        for (ProcessInfo processInfo : processInfoList) {
            if(processInfo.getCommand().matches(command)){
                isAlive = true;
                break;
            }
        }

        Response resp = agentRestClient.get(IAgentMonitorRest.class).updateStatus(
                new UpdateMonitorStatusParam(null, projectId, monitorId, isAlive ? EnumMonitorStatus.OK : EnumMonitorStatus.ERROR));
        if(!resp.isSuccess()){
            logger.error("update monitor [{}] status error: {}", monitorId, resp.getMessage());
            return;
        }

        logger.debug("update monitor [{}] status to [{}]", monitorId, isAlive);
    }
}
