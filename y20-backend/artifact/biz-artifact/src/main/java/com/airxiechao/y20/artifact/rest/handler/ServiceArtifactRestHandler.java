package com.airxiechao.y20.artifact.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.artifact.biz.api.IArtifactBiz;
import com.airxiechao.y20.artifact.rest.api.IServiceArtifactRest;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyPipelineFileParam;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyTemplateFileFromParam;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyTemplateFileToParam;
import com.airxiechao.y20.artifact.rest.param.ServiceRemoveTemplateFileParam;
import com.airxiechao.y20.common.core.biz.Biz;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceArtifactRestHandler implements IServiceArtifactRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceArtifactRestHandler.class);
    private IArtifactBiz artifactBiz = Biz.get(IArtifactBiz.class);

    @Override
    public Response copyPipelineFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCopyPipelineFileParam param = null;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCopyPipelineFileParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        if(!artifactBiz.existsPipelineFile(param.getUserId(), param.getFromProjectId(), param.getFromPipelineId())){
            return new Response();
        }

        boolean copied = artifactBiz.copyPipelineFile(
                param.getUserId(),
                param.getFromProjectId(), param.getFromPipelineId(),
                param.getToProjectId(), param.getToPipelineId());
        if(!copied){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public Response copyTemplateFileFrom(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCopyTemplateFileFromParam param = null;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCopyTemplateFileFromParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        if(!artifactBiz.existsPipelineFile(param.getUserId(), param.getProjectId(), param.getFromPipelineId())){
            return new Response();
        }

        boolean copied = artifactBiz.copyTemplateFileFrom(param.getUserId(), param.getProjectId(), param.getFromPipelineId(), param.getTemplateId());
        if(!copied){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public Response copyTemplateFileTo(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCopyTemplateFileToParam param = null;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCopyTemplateFileToParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        if(!artifactBiz.existsTemplateFile(param.getTemplateId())){
            return new Response();
        }

        boolean copied = artifactBiz.copyTemplateFileTo(param.getUserId(), param.getProjectId(), param.getToPipelineId(), param.getTemplateId());
        if(!copied){
            return new Response().error();
        }
        return new Response();
    }

    @Override
    public Response removeTemplateFile(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceRemoveTemplateFileParam param = null;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceRemoveTemplateFileParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean removed = artifactBiz.removeTemplateFile(param.getTemplateId());
        if(!removed){
            return new Response().error();
        }

        return new Response();
    }

}
