package com.airxiechao.y20.artifact.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.artifact.biz.api.IArtifactBiz;
import com.airxiechao.y20.artifact.rest.api.IAgentArtifactRest;
import com.airxiechao.y20.artifact.rest.param.*;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AgentArtifactRestHandler implements IAgentArtifactRest {

    private static final Logger logger = LoggerFactory.getLogger(AgentArtifactRestHandler.class);
    private IArtifactBiz artifactBiz = Biz.get(IArtifactBiz.class);

    @Override
    public Response<List<FsFile>> listUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListUserFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            return new Response().error("userid check fail");
        }

        List<FsFile> list = artifactBiz.listFile(path);

        return new Response().data(list);
    }

    @Override
    public Response uploadUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadUserFileParam param = null;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            return new Response().error("userid check fail");
        }

        try{
            artifactBiz.writeFile(path, param.getFile());
            return new Response();
        }catch (Exception e){
            logger.error("write user file error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public void downloadUserFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadUserFileParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadUserFileParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        String path = param.getPath();

        // check userid in path
        if(!checkUserId(param.getUserId(), path)){
            logger.error("userid check fail");
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, Paths.get(path).getFileName().toString());

        try(InputStream inputStream = artifactBiz.readFile(param.getPath())
        ){
            OutputStream outputStream = exchange.getOutputStream();

            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }catch (Exception e){
            logger.error("download user file error", e);
            return;
        }
    }

    private boolean checkUserId(Long userId, String path){
        Pattern pattern = Pattern.compile("/user/([^/]+)/.*");
        Matcher matcher = pattern.matcher(path);
        if(!matcher.find()) {
            return false;
        }

        Long userIdInPath = Long.valueOf(matcher.group(1));
        return userIdInPath == userId;
    }
}
