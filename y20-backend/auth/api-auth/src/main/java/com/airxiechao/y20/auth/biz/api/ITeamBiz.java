package com.airxiechao.y20.auth.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.auth.db.record.TeamJoinTokenRecord;
import com.airxiechao.y20.auth.db.record.TeamMemberRecord;
import com.airxiechao.y20.auth.db.record.TeamRecord;
import com.airxiechao.y20.auth.pojo.TeamJoinPrincipal;
import com.airxiechao.y20.auth.pojo.vo.TeamJoinTokenVo;
import com.airxiechao.y20.auth.pojo.vo.TeamMemberVo;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;

import java.util.List;

@IBiz
public interface ITeamBiz {
    TeamRecord getTeamByUserId(Long userId);
    TeamRecord getTeamByTeamId(Long teamId);
    TeamRecord getTeamByUserIdAndTeamId(Long userId, Long teamId);
    TeamRecord createTeam(Long userId);
    boolean deleteTeam(Long userId, Long teamId);
    TeamJoinTokenRecord createTeamJoinToken(Long userId, Long teamId) throws Exception;
    TeamJoinTokenVo getTeamJoinTokenByJoinTokenHashed(String joinTokenHashed);
    boolean deleteTeamJoinTokenUsed(Long teamJoinTokenId);
    TeamJoinPrincipal extractTeamJoinPrincipal(String teamJoinToken) throws Exception;
    JoinedTeamVo getJoinedTeam(Long userId, Long teamId);
    List<JoinedTeamVo> listJoinedTeam(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countJoinedTeam(Long userId);
    List<TeamMemberVo> listTeamMember(Long userId, Long teamId, String username, String orderField, String orderType, Integer pageNo, Integer pageSize);
    TeamMemberRecord getTeamMember(Long userId, Long teamId);
    long countTeamMember(Long userId, Long teamId, String username);
    TeamMemberRecord createTeamMember(Long userId, Long teamId);
    boolean deleteTeamMember(Long userId, Long teamId);
}
