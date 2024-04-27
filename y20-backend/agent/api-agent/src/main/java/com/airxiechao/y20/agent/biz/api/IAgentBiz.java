package com.airxiechao.y20.agent.biz.api;

import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;

import java.util.List;

@IBiz
public interface IAgentBiz {
    AgentRecord getByUserIdAndAgentId(Long userId, String agentId);
    AgentRecord getByAgentId(String agentId);
    AgentRecord getByClientId(String clientId);
    List<AgentRecord> list(Long userId,
        String agentId,
        String orderField,
        String orderType,
        Integer pageNo,
        Integer pageSize
    );

    long count(Long userId, String agentId);
    AgentRecord create(Long userId, String agentId);
    boolean updateClient(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os, String status);
    boolean updateUpgrading(Long userId, String agentId, boolean flagUpgrading);
    boolean updateRestarting(Long userId, String agentId, boolean flagRestarting);
    boolean deleteByUserIdAndAgentId(Long userId, String agentId);

    AgentVersionRecord getLatestVersion();
}
