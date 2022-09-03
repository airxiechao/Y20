package com.airxiechao.y20.template.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.process.transaction.TransactionPipeline;
import com.airxiechao.y20.artifact.rest.api.IServiceArtifactRest;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyTemplateFileFromParam;
import com.airxiechao.y20.artifact.rest.param.ServiceRemoveTemplateFileParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.Template;
import com.airxiechao.y20.template.rest.api.IServiceTemplateRest;
import com.airxiechao.y20.template.rest.param.ServiceCreateTemplateParam;
import com.airxiechao.y20.template.rest.param.ServiceUpdateTemplateParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServiceTemplateRestHandler implements IServiceTemplateRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTemplateRestHandler.class);

    private ITemplateBiz templateBiz = Biz.get(ITemplateBiz.class);

    @Override
    public Response<Long> createTemplate(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCreateTemplateParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCreateTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Long pipelineId = param.getPipelineId();
        TransactionPipeline pipeline = new TransactionPipeline(String.format("create template from pipeline [%d]", pipelineId));
        final String TRANSACTION_STORE_TEMPLATE_ID = "templateId";
        pipeline.addStep("create-template", (stepStore, tranStore, retStore, log) -> {
            // create record
            TemplateRecord templateRecord = templateBiz.create(
                    param.getName(),
                    param.getDescription(),
                    param.getUserId(),
                    param.getUsername(),
                    param.getSteps(),
                    param.getVariables()
            );
            if(null == templateRecord){
                throw new Exception("create template error");
            }

            Long templateId = templateRecord.getId();
            tranStore.put(TRANSACTION_STORE_TEMPLATE_ID, templateId);
        }, (stepStore, tranStore, retStore, log) -> {
            // delete record
            Long templateId = (Long)tranStore.get(TRANSACTION_STORE_TEMPLATE_ID);
            boolean deleted = templateBiz.delete(templateId);
            if(!deleted){
                log.error("delete record error");
            }
        }).addStep("copy-file", (stepStore, tranStore, retStore, log) -> {
            // copy file
            Long templateId = (Long)tranStore.get(TRANSACTION_STORE_TEMPLATE_ID);
            Response copyResp = ServiceRestClient.get(IServiceArtifactRest.class).copyTemplateFileFrom(
                    new ServiceCopyTemplateFileFromParam(
                            param.getUserId(),
                            param.getProjectId(),
                            param.getPipelineId(),
                            templateId
                    )
            );

            if(!copyResp.isSuccess()){
                logger.error(copyResp.getMessage());
                throw new Exception("copy template file error");
            }

            retStore.put(TRANSACTION_STORE_TEMPLATE_ID, templateId);

        }, (stepStore, tranStore, retStore, log) -> {

        });

        Response<Map> resp = pipeline.execute();
        if(!resp.isSuccess()){
            return new Response().error(resp.getMessage());
        }

        Long templateId = (Long)resp.getData().get(TRANSACTION_STORE_TEMPLATE_ID);

        return new Response<Long>().data(templateId);
    }

    @Override
    public Response<Long> updateTemplate(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceUpdateTemplateParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceUpdateTemplateParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Long templateId = param.getTemplateId();

        TemplateRecord record = templateBiz.get(param.getUserId(), templateId);
        if(null == record){
            return new Response().error("no template");
        }

        // update record
        Template template = record.toPojo();
        template.setName(param.getName());
        template.setDescription(param.getDescription());
        template.setSteps(param.getSteps());
        template.setVariables(param.getVariables());
        boolean recordUpdated = templateBiz.update(template.toRecord());
        if(!recordUpdated){
            return new Response().error("update template record error");
        }

        // delete file
        Response removeFileResp = ServiceRestClient.get(IServiceArtifactRest.class).removeTemplateFile(
                new ServiceRemoveTemplateFileParam(templateId));

        if(!removeFileResp.isSuccess()){
            logger.error("remove template [{}] file error: {}", templateId, removeFileResp.getMessage());
            return new Response().error("update template file error");
        }

        // copy file
        Response copyFileResp = ServiceRestClient.get(IServiceArtifactRest.class).copyTemplateFileFrom(
                new ServiceCopyTemplateFileFromParam(
                        param.getUserId(),
                        param.getProjectId(),
                        param.getPipelineId(),
                        templateId
                )
        );

        if(!copyFileResp.isSuccess()){
            logger.error("copy pipeline [{}] file to template [{}] error: {}", param.getPipelineId(), templateId, copyFileResp.getMessage());
            return new Response().error("update template file error");
        }

        return new Response<Long>().data(templateId);
    }
}
