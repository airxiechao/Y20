package com.airxiechao.y20.auth.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.auth.db.record.TeamJoinTokenRecord;
import com.airxiechao.y20.auth.db.record.TeamMemberRecord;
import com.airxiechao.y20.auth.db.record.TeamRecord;
import com.airxiechao.y20.auth.pojo.vo.TeamJoinTokenVo;
import com.airxiechao.y20.auth.pojo.vo.TeamMemberVo;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;

import java.util.List;

@IDb("mybatis-y20-auth.xml")
public interface ITeamDb {

    TeamRecord getTeamByUserId(Long userId);
    TeamRecord getTeamByTeamId(Long teamId);
    TeamRecord getTeamByUserIdANdTeamId(Long userId, Long teamId);

    boolean createTeam(TeamRecord teamRecord);
    boolean deleteTeam(Long userId, Long teamId);
    JoinedTeamVo getJoinedTeam(Long userId, Long teamId);
    List<JoinedTeamVo> listJoinedTeam(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countJoinedTeam(Long userId);

    TeamMemberRecord getTeamMember(Long userId, Long teamId);
    List<TeamMemberVo> listTeamMember(Long userId, Long teamId, String username, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countTeamMember(Long userId, Long teamId, String username);

    boolean createTeamJoinToken(TeamJoinTokenRecord teamJoinTokenRecord);
    TeamJoinTokenVo getTeamJoinTokenByJoinTokenHashed(String joinTokenHashed);
    boolean deleteTeamJoinToken(Long teamJoinTokenId);

    boolean createTeamMember(TeamMemberRecord teamMemberRecord);
    boolean deleteTeamMember(Long userId, Long teamId);
    boolean deleteAllTeamMember(Long teamId);
}
