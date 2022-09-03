package com.airxiechao.y20.auth.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.auth.pojo.vo.JoinedTeamVo;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IServiceTeamRest {

    @Get("/service/team/joined/get")
    @Auth(scope = EnumAccessScope.SERVICE)
    Response<JoinedTeamVo> getJoinedTeam(Object exc);
}
