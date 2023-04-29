package com.airxiechao.y20.scriptlib.rest.api;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.common.annotation.Auth;
import com.airxiechao.axcboot.communication.rest.annotation.Get;
import com.airxiechao.axcboot.communication.rest.annotation.Post;
import com.airxiechao.axcboot.core.annotation.IRest;
import com.airxiechao.y20.common.pojo.constant.auth.EnumAccessScope;
import com.airxiechao.y20.scriptlib.rest.param.*;

@IRest
public interface IUserScriptLibRest {
    @Get("/user/script/piece/get")
    @Auth(scope = EnumAccessScope.USER)
    Response get(GetScriptPieceParam param);

    @Get("/user/script/piece/list")
    @Auth(scope = EnumAccessScope.USER)
    Response list(ListScriptPieceParam param);

    @Post("/user/script/piece/create")
    @Auth(scope = EnumAccessScope.USER)
    Response create(CreateScriptPieceParam param);

    @Post("/user/script/piece/update")
    @Auth(scope = EnumAccessScope.USER)
    Response update(UpdateScriptPieceParam param);

    @Post("/user/script/piece/delete")
    @Auth(scope = EnumAccessScope.USER)
    Response delete(DeleteScriptPieceParam param);
}
