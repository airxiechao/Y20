package com.airxiechao.y20.scriptlib.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.scriptlib.biz.api.IScriptLibBiz;
import com.airxiechao.y20.scriptlib.pojo.ScriptPiece;
import com.airxiechao.y20.scriptlib.rest.api.IUserScriptLibRest;
import com.airxiechao.y20.scriptlib.rest.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserScriptLibRestHandler implements IUserScriptLibRest {

    private static final Logger logger = LoggerFactory.getLogger(UserScriptLibRestHandler.class);

    private IScriptLibBiz scriptLibBiz = Biz.get(IScriptLibBiz.class);

    @Override
    public Response get(GetScriptPieceParam param) {
        boolean isPublic = null != param.getPublic() && param.getPublic();
        ScriptPiece piece = scriptLibBiz.get(
                isPublic ? null : param.getUserId(),
                param.getScriptPieceId()
        );
        if(null == piece){
            return new Response().error("no script piece");
        }

        return new Response().data(piece);
    }

    @Override
    public Response list(ListScriptPieceParam param) {
        boolean isPublic = null != param.getPublic() && param.getPublic();
        List<ScriptPiece> list = scriptLibBiz.list(
                isPublic ? null : param.getUserId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        );

        long count = scriptLibBiz.count(isPublic ? null : param.getUserId(), param.getName());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response create(CreateScriptPieceParam param) {
        ScriptPiece piece = new ScriptPiece();
        piece.setUserId(param.getUserId());
        piece.setName(param.getName());
        piece.setScript(param.getScript());

        boolean created = scriptLibBiz.create(piece);
        if(!created){
            return new Response().error("create script piece error");
        }

        return new Response();
    }

    @Override
    public Response update(UpdateScriptPieceParam param) {
        ScriptPiece piece = scriptLibBiz.get(param.getUserId(), param.getScriptPieceId());
        if(null == piece){
            return new Response().error("no script piece");
        }

        piece.setName(param.getName());
        piece.setScript(param.getScript());

        boolean updated = scriptLibBiz.update(piece);
        if(!updated){
            return new Response().error("update script piece error");
        }

        return new Response();
    }

    @Override
    public Response delete(DeleteScriptPieceParam param) {
        ScriptPiece piece = scriptLibBiz.get(param.getUserId(), param.getScriptPieceId());
        if(null == piece){
            return new Response().error("no script piece");
        }

        boolean deleted = scriptLibBiz.delete(param.getUserId(), param.getScriptPieceId());
        if(!deleted){
            return new Response().error("delete script piece error");
        }

        return new Response();
    }

}
