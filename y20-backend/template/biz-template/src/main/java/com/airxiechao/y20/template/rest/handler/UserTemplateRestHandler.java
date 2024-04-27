package com.airxiechao.y20.template.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.process.transaction.TransactionPipeline;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.artifact.rest.api.IServiceArtifactRest;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyTemplateFileToParam;
import com.airxiechao.y20.artifact.rest.param.ServiceRemoveTemplateFileParam;
import com.airxiechao.y20.auth.pojo.AccessPrincipal;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCreatePipelineParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceDeletePipelineParam;
import com.airxiechao.y20.project.rest.api.IServiceProjectRest;
import com.airxiechao.y20.project.rest.param.ServiceExistProjectParam;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.Template;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateActionType;
import com.airxiechao.y20.template.pojo.pubsub.event.EventTemplateActivity;
import com.airxiechao.y20.template.pojo.vo.TemplateBasicVo;
import com.airxiechao.y20.template.rest.api.IUserTemplateRest;
import com.airxiechao.y20.template.rest.param.*;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserTemplateRestHandler implements IUserTemplateRest {

    private static final Logger logger = LoggerFactory.getLogger(UserTemplateRestHandler.class);

    private ITemplateBiz templateBiz = Biz.get(ITemplateBiz.class);

    @Override
    public Response getBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetTemplateParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TemplateRecord record = templateBiz.get(null, param.getTemplateId());
        if(null == record){
            return new Response().error("no template");
        }

        TemplateBasicVo vo = new TemplateBasicVo(record);
        return new Response().data(vo);
    }

    @Override
    public Response getFull(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetTemplateParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TemplateRecord record = templateBiz.get(null, param.getTemplateId());
        if(null == record){
            return new Response().error("no template");
        }

        Template template = record.toPojo();

        // 记录点击日志
        try {
            AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
            Long userId = accessPrincipal.getUserId();
            EventBus.getInstance().publish(
                    new EventTemplateActivity(userId, param.getTemplateId(), EnumTemplateActionType.CLICK, null));
        } catch (Exception e) {

        }

        return new Response().data(template);
    }

    @Override
    public Response list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListTemplateParam param = null;
        try {
            param = RestUtil.queryData(exchange, ListTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<TemplateBasicVo> list = templateBiz.list(
                null,
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> new TemplateBasicVo(record)).collect(Collectors.toList());

        long count = templateBiz.count(null, param.getName());

        // 记录搜索日志
        try {
            AccessPrincipal accessPrincipal = EnhancedRestUtil.extractAccessPrincipal(exchange);
            Long userId = accessPrincipal.getUserId();
            if(!StringUtil.isBlank(param.getName())) {
                EventBus.getInstance().publish(
                        new EventTemplateActivity(userId, null, EnumTemplateActionType.SEARCH, param.getName()));
            }
        } catch (Exception e) {

        }

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response listMy(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListTemplateParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListTemplateParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<TemplateBasicVo> list = templateBiz.list(
                param.getUserId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> new TemplateBasicVo(record)).collect(Collectors.toList());

        long count = templateBiz.count(param.getUserId(), param.getName());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response recommend(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        RecommendTemplateParam param = null;
        try {
            param = RestUtil.queryData(exchange, RecommendTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<TemplateBasicVo> list = templateBiz.list(
                null,
                null,
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> new TemplateBasicVo(record)).collect(Collectors.toList());

        long count = templateBiz.count(null, null);

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response delete(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteTemplateParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeleteTemplateParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TemplateRecord record = templateBiz.get(param.getUserId(), param.getTemplateId());
        if(null == record){
            return new Response().error("no template");
        }

        // delete record
        boolean deleted = templateBiz.delete(param.getTemplateId());
        if(!deleted){
            return new Response().error("delete template error");
        }

        // delete file
        Response removeResp = ServiceRestClient.get(IServiceArtifactRest.class).removeTemplateFile(
                new ServiceRemoveTemplateFileParam(
                        record.getId()
                )
        );

        if(!removeResp.isSuccess()){
            logger.error("remove template file error: {}", removeResp.getMessage());
        }

        return new Response();
    }

    @Override
    public Response apply(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ApplyTemplateParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, ApplyTemplateParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        TemplateRecord record = templateBiz.get(null, param.getTemplateId());
        if(null == record){
            return new Response().error("no template");
        }

        // check target project exist
        Response existProjectResp = ServiceRestClient.get(IServiceProjectRest.class).exist(
                new ServiceExistProjectParam(param.getUserId(), param.getToProjectId()));
        if(!existProjectResp.isSuccess()){
            return new Response().error("project not exist");
        }

        Long templateId = record.getId();
        Template template = record.toPojo();

        TransactionPipeline pipeline = new TransactionPipeline(String.format("apply template [%d]", templateId));
        final String TRANSACTION_STORE_PIPELINE_ID = "pipelineId";
        pipeline.addStep("create-pipeline", (stepStore, tranStore, retStore, log) -> {
            // create pipeline
            Response<Long> pipelineResp = ServiceRestClient.get(IServicePipelineRest.class).createPipeline(
                    new ServiceCreatePipelineParam(
                            param.getUserId(),
                            param.getToProjectId(),
                            param.getName(),
                            template.getDescription(),
                            param.getFlagOneRun(),
                            template.getSteps(),
                            template.getVariables()
                    )
            );

            if(!pipelineResp.isSuccess()){
                log.error(pipelineResp.getMessage());
                throw new Exception(String.format("create pipeline from template [%d] error", param.getTemplateId()));
            }

            Long pipelineId = pipelineResp.getData();
            tranStore.put(TRANSACTION_STORE_PIPELINE_ID, pipelineId);
        }, (stepStore, tranStore, retStore, log) -> {
            // delete pipeline
            Long pipelineId = (Long)tranStore.get(TRANSACTION_STORE_PIPELINE_ID);
            Response deletePipelineResp = ServiceRestClient.get(IServicePipelineRest.class).deletePipeline(
                    new ServiceDeletePipelineParam(pipelineId));
            if(!deletePipelineResp.isSuccess()){
                log.error("delete pipeline [{}] error: {}", pipelineId, deletePipelineResp.getMessage());
            }
        }).addStep("copy-file", (stepStore, tranStore, retStore, log) -> {
            // copy file
            Long pipelineId = (Long)tranStore.get(TRANSACTION_STORE_PIPELINE_ID);
            Response copyFileResp = ServiceRestClient.get(IServiceArtifactRest.class).copyTemplateFileTo(
                    new ServiceCopyTemplateFileToParam(
                            param.getUserId(),
                            param.getToProjectId(),
                            pipelineId,
                            param.getTemplateId()
                    )
            );

            if(!copyFileResp.isSuccess()){
                logger.error(copyFileResp.getMessage());
                throw new Exception(String.format("copy template [%d] file to pipeline [%d] error", param.getTemplateId(), pipelineId));
            }

            retStore.put(TRANSACTION_STORE_PIPELINE_ID, pipelineId);
        }, (stepStore, tranStore, retStore, log) -> {

        });

        Response<Map> resp = pipeline.execute();
        if(!resp.isSuccess()){
            return new Response().error(resp.getMessage());
        }

        // add num apply
        Integer numApply = record.getNumApply();
        if(null == numApply){
            numApply = 0;
        }
        record.setNumApply(numApply + 1);
        templateBiz.updateNumApply(record);

        Long pipelineId = (Long)resp.getData().get(TRANSACTION_STORE_PIPELINE_ID);

        // 记录应用日志
        try {
            EventBus.getInstance().publish(
                    new EventTemplateActivity(param.getUserId(), param.getTemplateId(), EnumTemplateActionType.APPLY, null));
        }catch (Exception e){

        }

        return new Response().data(pipelineId);
    }
}
