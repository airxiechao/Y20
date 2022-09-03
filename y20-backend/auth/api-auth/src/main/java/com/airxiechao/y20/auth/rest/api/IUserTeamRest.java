package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserTeamRest {

    @Get("/user/team/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getTeam(Object exc);

    @Post("/user/team/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createTeam(Object exc);

    @Post("/user/team/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deleteTeam(Object exc);

    @Get("/user/team/join/token/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getTeamJoinToken(Object exc);

    @Post("/user/team/join/token/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createTeamJoinToken(Object exc);

    @Get("/user/team/member/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listTeamMember(Object exc);

    @Post("/user/team/member/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response deleteTeamMember(Object exc);

    @Post("/user/team/join")
    @Auth(scope = EnumAccessScope.USER)
    Response joinTeam(Object exc);

    @Post("/user/team/leave")
    @Auth(scope = EnumAccessScope.USER)
    Response leaveTeam(Object exc);

    @Get("/user/team/joined/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listJoinedTeam(Object exc);
}
