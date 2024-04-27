package com.airxiechao.y20.agent.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.agent.biz.api.IAgentBiz;
import com.airxiechao.y20.agent.db.api.IAgentDb;
import com.airxiechao.y20.agent.db.record.AgentRecord;
import com.airxiechao.y20.agent.db.record.AgentVersionRecord;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.common.pojo.config.CommonConfig;

import java.util.Date;
import java.util.List;

import static com.airxiechao.y20.agent.pojo.constant.EnumAgentStatus.STATUS_ONLINE;

public class AgentBizProcess implements IAgentBiz {
    private static final CommonConfig commonConfig = ConfigFactory.get(CommonConfig.class);
    private IAgentDb agentDb = Db.get(IAgentDb.class);

    @Override
    public AgentRecord getByUserIdAndAgentId(Long userId, String agentId) {
        return agentDb.getByUserIdAndAgentId(userId, agentId);
    }

    @Override
    public AgentRecord getByAgentId(String agentId) {
        return agentDb.getByAgentId(agentId);
    }

    @Override
    public AgentRecord getByClientId(String clientId) {
        return agentDb.getByClientId(clientId);
    }

    @Override
    public List<AgentRecord> list(Long userId, String agentId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return agentDb.list(userId, agentId, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long count(Long userId, String agentId) {
        return agentDb.count(userId, agentId);
    }

    @Override
    public AgentRecord create(Long userId, String agentId) {
        AgentRecord record = new AgentRecord();
        record.setUserId(userId);
        record.setAgentId(agentId);

        boolean created = agentDb.insert(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public boolean updateClient(Long userId, String agentId, String clientId, String version, String ip, String hostName, String os, String status){
        AgentRecord record = agentDb.getByUserIdAndAgentId(userId, agentId);
        if(null == record){
            return false;
        }

        record.setClientId(clientId);
        record.setStatus(status);
        if(STATUS_ONLINE.equals(status)){
            record.setVersion(version);
            record.setIp(ip);
            record.setHostName(hostName);
            record.setOs(os);
            record.setFlagUpgrading(false);
            record.setFlagRestarting(false);
            record.setLastHeartbeatTime(new Date());
        }

        boolean updated = agentDb.update(record);
        return updated;
    }

    @Override
    public boolean updateUpgrading(Long userId, String agentId, boolean flagUpgrading) {
        AgentRecord record = agentDb.getByUserIdAndAgentId(userId, agentId);
        if(null == record){
            return false;
        }

        record.setFlagUpgrading(flagUpgrading);

        boolean updated = agentDb.update(record);
        return updated;
    }

    @Override
    public boolean updateRestarting(Long userId, String agentId, boolean flagRestarting) {
        AgentRecord record = agentDb.getByUserIdAndAgentId(userId, agentId);
        if(null == record){
            return false;
        }

        record.setFlagRestarting(flagRestarting);

        boolean updated = agentDb.update(record);
        return updated;
    }

    @Override
    public boolean deleteByUserIdAndAgentId(Long userId, String agentId) {
        return agentDb.deleteByUserIdAndAgentId(userId, agentId);
    }

    @Override
    public AgentVersionRecord getLatestVersion() {
        AgentVersionRecord record = agentDb.getLatestVersion();
        if(null != record) {
            record.setDownloadWinUrl(String.format("%s%s", commonConfig.getServer().getUrl(), record.getDownloadWinUrl()));
            record.setDownloadLinuxUrl(String.format("%s%s", commonConfig.getServer().getUrl(), record.getDownloadLinuxUrl()));
            record.setDownloadWinUpgraderUrl(String.format("%s%s", commonConfig.getServer().getUrl(), record.getDownloadWinUpgraderUrl()));
            record.setDownloadLinuxUpgraderUrl(String.format("%s%s", commonConfig.getServer().getUrl(), record.getDownloadLinuxUpgraderUrl()));
        }

        return record;
    }

}
