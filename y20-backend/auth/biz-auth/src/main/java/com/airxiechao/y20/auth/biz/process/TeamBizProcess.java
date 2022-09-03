package com.airxiechao.y20.auth.biz.process;

import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.auth.biz.api.ITeamBiz;
import com.airxiechao.y20.auth.db.api.ITeamDb;
import com.airxiechao.y20.auth.db.record.TeamJoinTokenRecord;
import com.airxiechao.y20.auth.db.record.TeamMemberRecord;
import com.airxiechao.y20.auth.db.record.TeamRecord;
import com.airxiechao.y20.auth.pojo.TeamJoinPrincipal;
import com.airxiechao.y20.auth.pojo.vo.TeamJoinTokenVo;
import com.airxiechao.y20.auth.pojo.vo.TeamMemberVo;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;
import com.airxiechao.y20.auth.util.AuthUtil;
import com.airxiechao.y20.common.core.db.Db;
import com.alibaba.fastjson.JSON;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeamBizProcess implements ITeamBiz {

    private ITeamDb teamDb = Db.get(ITeamDb.class);

    @Override
    public TeamRecord getTeamByUserId(Long userId) {
        return teamDb.getTeamByUserId(userId);
    }

    @Override
    public TeamRecord getTeamByTeamId(Long teamId) {
        return teamDb.getTeamByTeamId(teamId);
    }

    @Override
    public TeamRecord getTeamByUserIdAndTeamId(Long userId, Long teamId) {
        return teamDb.getTeamByUserIdANdTeamId(userId, teamId);
    }

    @Override
    public TeamRecord createTeam(Long userId) {
        TeamRecord teamRecord = new TeamRecord();
        teamRecord.setUserId(userId);
        teamRecord.setCreateTime(new Date());

        boolean created = teamDb.createTeam(teamRecord);
        if(!created){
            return null;
        }

        return teamRecord;
    }

    @Override
    public boolean deleteTeam(Long userId, Long teamId) {
        boolean deleted = teamDb.deleteTeam(userId, teamId) && teamDb.deleteAllTeamMember(teamId);
        return deleted;
    }

    @Override
    public TeamJoinTokenRecord createTeamJoinToken(Long userId, Long teamId) throws Exception {
        Date beginTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginTime);
        cal.add(Calendar.HOUR, 24);
        Date endTime = cal.getTime();

        TeamJoinPrincipal teamJoinPrincipal = new TeamJoinPrincipal();
        teamJoinPrincipal.setTeamJoinUuid(UuidUtil.random());
        teamJoinPrincipal.setTeamJoinUserId(userId);
        teamJoinPrincipal.setTeamJoinTeamId(teamId);
        teamJoinPrincipal.setTeamJoinBeginTime(beginTime);
        teamJoinPrincipal.setTeamJoinEndTime(endTime);

        String joinToken = AuthUtil.encrypt(JSON.toJSONString(teamJoinPrincipal));
        String joinTokenHashed = AuthUtil.hash(joinToken);

        TeamJoinTokenRecord joinTokenRecord = new TeamJoinTokenRecord();
        joinTokenRecord.setUserId(userId);
        joinTokenRecord.setTeamId(teamId);
        joinTokenRecord.setBeginTime(beginTime);
        joinTokenRecord.setEndTime(endTime);
        joinTokenRecord.setCreateTime(beginTime);
        joinTokenRecord.setJoinTokenHashed(joinTokenHashed);

        boolean created = teamDb.createTeamJoinToken(joinTokenRecord);
        if(!created){
            throw new Exception("create team join token error");
        }

        return joinTokenRecord;
    }

    @Override
    public TeamJoinTokenVo getTeamJoinTokenByJoinTokenHashed(String joinTokenHashed) {
        TeamJoinTokenVo teamJoinTokenVo = teamDb.getTeamJoinTokenByJoinTokenHashed(joinTokenHashed);
        return teamJoinTokenVo;
    }

    @Override
    public boolean deleteTeamJoinTokenUsed(Long teamJoinTokenId) {
        return teamDb.deleteTeamJoinToken(teamJoinTokenId);
    }

    @Override
    public TeamJoinPrincipal extractTeamJoinPrincipal(String teamJoinToken) throws Exception {
        String token;
        try {
            token = AuthUtil.decrypt(teamJoinToken);
        } catch (Exception e) {
            throw new Exception("invalid team join token");
        }
        TeamJoinPrincipal teamJoinPrincipal = JSON.parseObject(token, TeamJoinPrincipal.class);

        if(teamJoinPrincipal.getTeamJoinEndTime().before(new Date())){
            throw new Exception("team join token expired");
        }

        return teamJoinPrincipal;
    }

    @Override
    public JoinedTeamVo getJoinedTeam(Long userId, Long teamId) {
        return teamDb.getJoinedTeam(userId, teamId);
    }

    @Override
    public List<JoinedTeamVo> listJoinedTeam(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        List<JoinedTeamVo> teamVos = teamDb.listJoinedTeam(userId, orderField, orderType, pageNo, pageSize);
        return teamVos;
    }

    @Override
    public long countJoinedTeam(Long userId) {
        return teamDb.countJoinedTeam(userId);
    }

    @Override
    public List<TeamMemberVo> listTeamMember(Long userId, Long teamId, String username, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        List<TeamMemberVo> teamMemberVos = teamDb.listTeamMember(userId, teamId, username, orderField, orderType, pageNo, pageSize);
        return teamMemberVos;
    }

    @Override
    public TeamMemberRecord getTeamMember(Long userId, Long teamId) {
        return teamDb.getTeamMember(userId, teamId);
    }

    @Override
    public long countTeamMember(Long userId, Long teamId, String username) {
        return teamDb.countTeamMember(userId, teamId, username);
    }

    @Override
    public TeamMemberRecord createTeamMember(Long userId, Long teamId) {
        TeamMemberRecord teamMemberRecord = new TeamMemberRecord();
        teamMemberRecord.setUserId(userId);
        teamMemberRecord.setTeamId(teamId);
        teamMemberRecord.setJoinTime(new Date());

        boolean created = teamDb.createTeamMember(teamMemberRecord);
        if(!created){
            return null;
        }

        return teamMemberRecord;
    }

    @Override
    public boolean deleteTeamMember(Long userId, Long teamId) {
        return teamDb.deleteTeamMember(userId, teamId);
    }

}
