package com.airxiechao.y20.agent.db.api;

import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;

import java.util.List;

@IDb("mybatis-y20-agent.xml")
public interface IAgentDb {
    AgentRecord getByUserIdAndAgentId(Long userId, String agentId);
    AgentRecord getByAgentId(String agentId);
    AgentRecord getByClientId(String clientId);
    List<AgentRecord> list(
            Long userId,
            String agentId,
            String orderField,
            String orderType,
            Integer pageNo,
            Integer pageSize
    );
    long count(Long userId, String agentId);
    boolean insert(AgentRecord agentRecord);
    boolean update(AgentRecord agentRecord);
    boolean deleteByUserIdAndAgentId(Long userId, String agentId);

    AgentVersionRecord getLatestVersion();
}
