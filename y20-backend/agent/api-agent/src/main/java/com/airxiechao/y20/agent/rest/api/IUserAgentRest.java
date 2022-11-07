package com.airxiechao.y20.agent.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;

@IRest
public interface IUserAgentRest {

    @Get("/user/agent/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listAgent(Object exc);

    @Get("/user/agent/detail/list")
    @Auth(scope = EnumAccessScope.USER)
    Response listDetailAgent(Object exc);

    @Get("/user/agent/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getAgent(Object exc);

    @Post("/user/agent/create")
    @Auth(scope = EnumAccessScope.USER)
    Response createAgent(Object exc);

    // agent version

    @Get("/user/agent/version/latest/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getLatestAgentVersion(Object exc);

    @Post("/user/agent/upgrade")
    @Auth(scope = EnumAccessScope.USER)
    Response upgradeAgent(Object exc);

    @Post("/user/agent/uninstall")
    @Auth(scope = EnumAccessScope.USER)
    Response uninstallAgent(Object exc);

    @Post("/user/agent/restart")
    @Auth(scope = EnumAccessScope.USER)
    Response restartAgent(Object exc);

    @Get("/user/agent/config/read")
    @Auth(scope = EnumAccessScope.USER)
    Response readAgentConfig(Object exc);

    @Get("/user/agent/access/token/get")
    @Auth(scope = EnumAccessScope.USER)
    Response getAgentAccessToken(Object exc);

    @Post("/user/agent/config/save")
    @Auth(scope = EnumAccessScope.USER)
    Response saveAgentConfig(Object exc);

    @Post("/user/agent/clean")
    @Auth(scope = EnumAccessScope.USER)
    Response cleanAgentConfig(Object exc);

    // agent join script

    @Post("/user/agent/join/script/generate")
    @Auth(scope = EnumAccessScope.USER)
    Response generateAgentJoinScript(Object exc);
}
