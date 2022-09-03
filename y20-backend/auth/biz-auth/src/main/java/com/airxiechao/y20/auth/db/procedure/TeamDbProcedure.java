package com.airxiechao.y20.auth.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.auth.db.api.ITeamDb;
import com.airxiechao.y20.auth.db.record.TeamJoinTokenRecord;
import com.airxiechao.y20.auth.db.record.TeamMemberRecord;
import com.airxiechao.y20.auth.db.record.TeamRecord;
import com.airxiechao.y20.auth.pojo.vo.TeamJoinTokenVo;
import com.airxiechao.y20.auth.pojo.vo.TeamMemberVo;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;

import java.util.List;

public class TeamDbProcedure extends AbstractDbProcedure implements ITeamDb {

    public TeamDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public TeamRecord getTeamByUserId(Long userId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamRecord.class))
                .where(DbUtil.column(TeamRecord.class, "userId"), "=", userId)
                .build();

        TeamRecord teamRecord = this.dbManager.selectFirstBySql(sqlParams, TeamRecord.class);

        return teamRecord;
    }

    @Override
    public TeamRecord getTeamByTeamId(Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamRecord.class))
                .where(DbUtil.column(TeamRecord.class, "id"), "=", teamId)
                .build();

        TeamRecord teamRecord = this.dbManager.selectFirstBySql(sqlParams, TeamRecord.class);

        return teamRecord;
    }

    @Override
    public TeamRecord getTeamByUserIdANdTeamId(Long userId, Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamRecord.class))
                .where(DbUtil.column(TeamRecord.class, "id"), "=", teamId)
                .where(DbUtil.column(TeamRecord.class, "userId"), "=", userId)
                .build();

        TeamRecord teamRecord = this.dbManager.selectFirstBySql(sqlParams, TeamRecord.class);

        return teamRecord;
    }

    @Override
    public boolean createTeam(TeamRecord teamRecord) {
        return dbManager.insert(teamRecord) > 0;
    }

    @Override
    public boolean deleteTeam(Long userId, Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(TeamRecord.class))
                .where(DbUtil.column(TeamRecord.class, "id"), "=", teamId)
                .where(DbUtil.column(TeamRecord.class, "userId"), "=", userId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, TeamRecord.class) > 0;
        return deleted;
    }

    @Override
    public JoinedTeamVo getJoinedTeam(Long userId, Long teamId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("team.*, team.id as team_id, user.username, team_member.join_time as join_time")
                .from(DbUtil.table(TeamMemberRecord.class))
                .join("left join team on team.id = team_member.team_id")
                .join("left join user on user.id = team.user_id")
                .where("team_member.user_id", "=", userId)
                .where("team_member.team_id", "=", teamId);

        SqlParams sqlParams = sqlParamsBuilder.build();

        JoinedTeamVo teamVo = this.dbManager.selectFirstBySql(sqlParams, JoinedTeamVo.class);

        return teamVo;
    }

    @Override
    public List<JoinedTeamVo> listJoinedTeam(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("team.*, team.id as team_id, user.username, team_member.join_time as join_time")
                .from(DbUtil.table(TeamMemberRecord.class))
                .join("left join team on team.id = team_member.team_id")
                .join("left join user on user.id = team.user_id")
                .where("team_member.user_id", "=", userId);

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(StringUtil.camelCaseToUnderscore(orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        List<JoinedTeamVo> teamVos = this.dbManager.selectBySql(sqlParams, JoinedTeamVo.class);

        return teamVos;
    }

    @Override
    public long countJoinedTeam(Long userId) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamMemberRecord.class))
                .where(DbUtil.column(TeamMemberRecord.class, "userId"), "=", userId)
                .count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, TeamMemberRecord.class);
    }

    @Override
    public TeamMemberRecord getTeamMember(Long userId, Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamMemberRecord.class))
                .where(DbUtil.column(TeamMemberRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(TeamMemberRecord.class, "teamId"), "=", teamId)
                .build();

        TeamMemberRecord teamMemberRecord = this.dbManager.selectFirstBySql(sqlParams, TeamMemberRecord.class);

        return teamMemberRecord;
    }

    @Override
    public List<TeamMemberVo> listTeamMember(Long userId, Long teamId, String username, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("team_member.*, team_member.id as team_member_id, user.username")
                .from(DbUtil.table(TeamMemberRecord.class))
                .join("left join team on team.id = team_member.team_id")
                .join("left join user on user.id = team_member.user_id")
                .where("team.user_id", "=", userId)
                .where("team.id", "=", teamId);

        if(!StringUtil.isBlank(username)){
            sqlParamsBuilder.where("user.username", "like", "%" + username + "%");
        }

        if(!StringUtil.isBlank(orderField) && !StringUtil.isBlank(orderType)){
            sqlParamsBuilder.order(StringUtil.camelCaseToUnderscore(orderField), orderType);
        }

        if(null != pageNo && null != pageSize) {
            sqlParamsBuilder.page(pageNo, pageSize);
        }

        SqlParams sqlParams = sqlParamsBuilder.build();

        List<TeamMemberVo> teamMemberVos = this.dbManager.selectBySql(sqlParams, TeamMemberVo.class);

        return teamMemberVos;
    }

    @Override
    public long countTeamMember(Long userId, Long teamId, String username) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(TeamMemberRecord.class))
                .join("left join team on team.id = team_member.team_id")
                .join("left join user on user.id = team_member.user_id")
                .where("team.user_id", "=", userId)
                .where("team.id", "=", teamId);

        if(!StringUtil.isBlank(username)){
            sqlParamsBuilder.where("user.username", "like", "%" + username + "%");
        }

        sqlParamsBuilder.count();

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.longBySql(sqlParams, TeamMemberRecord.class);
    }

    @Override
    public boolean createTeamJoinToken(TeamJoinTokenRecord teamJoinTokenRecord) {
        return dbManager.insert(teamJoinTokenRecord) > 0;
    }

    @Override
    public TeamJoinTokenVo getTeamJoinTokenByJoinTokenHashed(String joinTokenHashed) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .select("team_join_token.*, user.username")
                .from(DbUtil.table(TeamJoinTokenRecord.class))
                .join("left join user on user.id = team_join_token.user_id")
                .where(DbUtil.column(TeamJoinTokenRecord.class, "joinTokenHashed"), "=", joinTokenHashed)
                .build();
        return this.dbManager.selectFirstBySql(sqlParams, TeamJoinTokenVo.class);
    }

    @Override
    public boolean deleteTeamJoinToken(Long teamJoinTokenId) {
        return this.dbManager.deleteById(teamJoinTokenId, TeamJoinTokenRecord.class) > 0;
    }

    @Override
    public boolean createTeamMember(TeamMemberRecord teamMemberRecord) {
        return this.dbManager.insert(teamMemberRecord) > 0;
    }

    @Override
    public boolean deleteTeamMember(Long userId, Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(TeamMemberRecord.class))
                .where(DbUtil.column(TeamMemberRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(TeamMemberRecord.class, "teamId"), "=", teamId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, TeamMemberRecord.class) > 0;
        return deleted;
    }

    @Override
    public boolean deleteAllTeamMember(Long teamId) {
        SqlParams sqlParams = new SqlParamsBuilder()
                .delete()
                .from(DbUtil.table(TeamMemberRecord.class))
                .where(DbUtil.column(TeamMemberRecord.class, "teamId"), "=", teamId)
                .build();

        boolean deleted = this.dbManager.deleteBySql(sqlParams, TeamMemberRecord.class) > 0;
        return deleted;
    }
}
