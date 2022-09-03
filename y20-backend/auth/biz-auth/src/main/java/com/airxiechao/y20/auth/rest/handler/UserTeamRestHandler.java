package com.airxiechao.y20.auth.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.auth.biz.api.ITeamBiz;
import com.airxiechao.y20.auth.db.record.TeamJoinTokenRecord;
import com.airxiechao.y20.auth.db.record.TeamMemberRecord;
import com.airxiechao.y20.auth.db.record.TeamRecord;
import com.airxiechao.y20.auth.pojo.TeamJoinPrincipal;
import com.airxiechao.y20.auth.pojo.vo.TeamJoinTokenVo;
import com.airxiechao.y20.auth.pojo.vo.TeamMemberVo;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;
import com.airxiechao.y20.auth.rest.api.IUserTeamRest;
import com.airxiechao.y20.auth.rest.param.*;
import com.airxiechao.y20.auth.util.AuthRestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class UserTeamRestHandler implements IUserTeamRest {

    private static final Logger logger = LoggerFactory.getLogger(UserTeamRestHandler.class);
    private ITeamBiz teamBiz = Biz.get(ITeamBiz.class);

    @Override
    public Response getTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetTeamParam param = null;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, GetTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamRecord teamRecord = teamBiz.getTeamByUserId(param.getUserId());
        if(null == teamRecord){
            // create team
            teamRecord = teamBiz.createTeam(param.getUserId());
            if(null == teamRecord){
                return new Response().error();
            }
        }

        return new Response().data(teamRecord);
    }

    @Override
    public Response createTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateTeamParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, CreateTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamRecord teamRecord = teamBiz.createTeam(param.getUserId());
        if(null == teamRecord){
            return new Response().error();
        }

        return new Response().data(teamRecord.getId());
    }

    @Override
    public Response deleteTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteTeamParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, DeleteTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean deleted = teamBiz.deleteTeam(param.getUserId(), param.getTeamId());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response getTeamJoinToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetTeamJoinTokenParam param = null;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, GetTeamJoinTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamJoinTokenVo teamJoinTokenVo = teamBiz.getTeamJoinTokenByJoinTokenHashed(param.getJoinTokenHashed());
        if(null == teamJoinTokenVo){
            return new Response().error("no team join token");
        }

        return new Response().data(teamJoinTokenVo);
    }

    @Override
    public Response createTeamJoinToken(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateTeamJoinTokenParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, CreateTeamJoinTokenParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamRecord teamRecord = teamBiz.getTeamByUserId(param.getUserId());
        if(null == teamRecord){
            return new Response().error("no team");
        }

        try {
            TeamJoinTokenRecord teamJoinTokenRecord = teamBiz.createTeamJoinToken(param.getUserId(), teamRecord.getId());
            return new Response().data(teamJoinTokenRecord.getJoinTokenHashed());
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response listTeamMember(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListTeamMemberParam param = null;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, ListTeamMemberParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamRecord teamRecord = teamBiz.getTeamByUserId(param.getUserId());
        if(null == teamRecord){
            return new Response().error("no team");
        }

        List<TeamMemberVo> list = teamBiz.listTeamMember(
                param.getUserId(),
                teamRecord.getId(),
                param.getUsername(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        );

        long count = teamBiz.countTeamMember(param.getUserId(), teamRecord.getId(), param.getUsername());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response deleteTeamMember(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteTeamMemberParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, DeleteTeamMemberParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamRecord teamRecord = teamBiz.getTeamByUserId(param.getUserId());
        if(null == teamRecord){
            return new Response().error("no team");
        }

        boolean deleted = teamBiz.deleteTeamMember(param.getMemberUserId(), teamRecord.getId());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response joinTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        JoinTeamParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, JoinTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamJoinTokenVo teamJoinTokenVo = teamBiz.getTeamJoinTokenByJoinTokenHashed(param.getJoinTokenHashed());
        if(null == teamJoinTokenVo){
            return new Response().error("no team join token");
        }

        if(teamJoinTokenVo.getEndTime().before(new Date())){
            return new Response().error("team join token expired");
        }

        if( teamJoinTokenVo.getUserId() == param.getUserId() ||
            null != teamBiz.getTeamMember(param.getUserId(), teamJoinTokenVo.getTeamId())
        ){
            return new Response().error("user already in team");
        }

        TeamMemberRecord teamMemberRecord = teamBiz.createTeamMember(param.getUserId(), teamJoinTokenVo.getTeamId());
        if(null == teamMemberRecord){
            return new Response().error();
        }

        teamBiz.deleteTeamJoinTokenUsed(teamJoinTokenVo.getId());

        return new Response();
    }

    @Override
    public Response leaveTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        LeaveTeamParam param = null;
        try {
            param = AuthRestUtil.rawJsonDataWithAccessPrincipal(exchange, LeaveTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TeamMemberRecord teamMemberRecord = teamBiz.getTeamMember(param.getUserId(), param.getLeaveTeamId());
        if(null == teamMemberRecord){
            return new Response().error("no team member");
        }

        boolean deleted = teamBiz.deleteTeamMember(param.getUserId(), param.getLeaveTeamId());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response listJoinedTeam(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListJoinedTeamParam param = null;
        try {
            param = AuthRestUtil.queryDataWithAccessPrincipal(exchange, ListJoinedTeamParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<JoinedTeamVo> list = teamBiz.listJoinedTeam(
                param.getUserId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        );

        long count = teamBiz.countJoinedTeam(param.getUserId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }
}
