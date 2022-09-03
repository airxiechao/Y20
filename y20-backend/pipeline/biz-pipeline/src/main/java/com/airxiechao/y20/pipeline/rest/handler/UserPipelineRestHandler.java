package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.process.transaction.TransactionPipeline;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.y20.artifact.rest.api.IServiceArtifactRest;
import com.airxiechao.y20.artifact.rest.param.ServiceCopyPipelineFileParam;
import com.airxiechao.y20.auth.pojo.vo.AccountVo;
import com.airxiechao.y20.auth.rest.api.IServiceAccountRest;
import com.airxiechao.y20.auth.rest.param.ServiceGetAccountParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServicePipelineRunInstanceRestClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.constant.code.EnumRespCode;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.*;
import com.airxiechao.y20.pipeline.pojo.*;
import com.airxiechao.y20.pipeline.pojo.constant.*;
import com.airxiechao.y20.pipeline.pojo.exception.NoEnoughQuotaException;
import com.airxiechao.y20.pipeline.pojo.exception.OnlyOneRunException;
import com.airxiechao.y20.pipeline.pojo.vo.*;
import com.airxiechao.y20.pipeline.rest.api.IServiceExplorerRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.api.IServiceTerminalRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.*;
import com.airxiechao.y20.pipeline.rest.api.IUserPipelineRest;
import com.airxiechao.y20.project.rest.api.IServiceProjectRest;
import com.airxiechao.y20.project.rest.param.ServiceExistProjectParam;
import com.airxiechao.y20.template.rest.api.IServiceTemplateRest;
import com.airxiechao.y20.template.rest.param.ServiceCreateTemplateParam;
import com.airxiechao.y20.template.rest.param.ServiceUpdateTemplateParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class UserPipelineRestHandler implements IUserPipelineRest {

    private static final Logger logger = LoggerFactory.getLogger(UserPipelineRestHandler.class);
    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    @Override
    public Response listPipeline(Object exc){
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<PipelineWithLastRunVo> list = pipelineBiz.listWithLastRun(
                param.getUserId(),
                param.getProjectId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        );

        long count = pipelineBiz.count(param.getUserId(), param.getProjectId(), param.getName());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response createPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreatePipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.create(param.getUserId(), param.getProjectId(), param.getName(), null, param.getFlagOneRun(), null, null);
        if(null == record){
            return new Response().error();
        }

        return new Response().data(record.getId());
    }

    @Override
    public Response createPipelineHello(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineHelloParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreatePipelineHelloParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // steps
        List<PipelineStep> steps = new ArrayList<>();
        steps.add(new PipelineStep(){{
            setName("准备环境");
            setType(EnumStepRunType.REMOTE_PREPARE_ENV_TYPE);
            setParams(new HashMap<>(){{
                put("syncPipelineFile", true);
                put("syncProjectFile", false);
                put("agentId", "{{AGENT}}");
                put("env", new HashMap<>());
            }});
            setDisabled(false);
        }});
        steps.add(new PipelineStep(){{
            setName("执行脚本");
            setType(EnumStepRunType.REMOTE_SCRIPT_TYPE);
            setParams(new HashMap<>(){{
                put("script", "echo hello world");
            }});
            setDisabled(false);
        }});

        // variables
        Map<String, PipelineVariable> variables = new HashMap<>();
        variables.put("AGENT", new PipelineVariable(){{
            setName("AGENT");
            setKind(EnumPipelineVariableKind.IN);
            setParamType(EnumPipelineParamType.SELECT_AGENT);
            setRequired(true);
            setPassword(false);
        }});

        PipelineRecord record = pipelineBiz.create(
                param.getUserId(),
                param.getProjectId(),
                param.getName(),
                "# 打印hello world\n\n## 输入参数：\nAGENT 节点",
                param.getFlagOneRun(),
                steps,
                variables
        );
        if(null == record){
            return new Response().error();
        }

        return new Response().data(record.getId());
    }

    @Override
    public Response publishPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        PublishPipelineParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, PublishPipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Response<AccountVo> accountVoResponse = ServiceRestClient.get(IServiceAccountRest.class).getAccount(
                new ServiceGetAccountParam(param.getUserId())
        );
        if(!accountVoResponse.isSuccess()){
            return new Response().error("no user");
        }

        AccountVo accountVo = accountVoResponse.getData();
        String username = accountVo.getUsername();

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );

        if(null == record){
            return new Response().error("no pipeline");
        }

        Pipeline pipeline = record.toPojo();

        Long templateId = param.getTemplateId();
        if(null == templateId){
            Response<Long> templateResp = ServiceRestClient.get(IServiceTemplateRest.class).createTemplate(
                    new ServiceCreateTemplateParam(
                            param.getName(),
                            record.getDescription(),
                            pipeline.getUserId(),
                            username,
                            pipeline.getProjectId(),
                            pipeline.getPipelineId(),
                            pipeline.getSteps(),
                            pipeline.getVariables()
                    )
            );

            if(!templateResp.isSuccess()){
                return new Response().error(templateResp.getMessage());
            }

            templateId = templateResp.getData();
        }else{
            Response<Long> templateResp = ServiceRestClient.get(IServiceTemplateRest.class).updateTemplate(
                    new ServiceUpdateTemplateParam(
                            param.getName(),
                            record.getDescription(),
                            pipeline.getUserId(),
                            username,
                            pipeline.getProjectId(),
                            pipeline.getPipelineId(),
                            templateId,
                            pipeline.getSteps(),
                            pipeline.getVariables()
                    )
            );

            if(!templateResp.isSuccess()){
                return new Response().error(templateResp.getMessage());
            }
        }

        // update pipeline published template
        boolean updated = pipelineBiz.updatePublishedTemplate(param.getUserId(), param.getProjectId(), param.getPipelineId(), templateId);
        if(!updated){
            logger.error("update pipeline [{}] published template [{}] error", param.getPipelineId(), templateId);
        }

        return new Response().data(templateId);
    }

    @Override
    public Response copyPipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CopyPipelineParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CopyPipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // check target project exist
        Response existProjectResp = ServiceRestClient.get(IServiceProjectRest.class).exist(
                new ServiceExistProjectParam(param.getUserId(), param.getToProjectId()));
        if(!existProjectResp.isSuccess()){
            return new Response().error("project not exist");
        }

        TransactionPipeline pipeline = new TransactionPipeline(String.format("copy pipeline [%d]", param.getPipelineId()));
        final String TRANSACTION_NEW_PIPELINE_ID = "newPipelineId";
        pipeline.addStep("copy-record", (stepStore, tranStore, retStore, log) -> {
            // copy record
            PipelineRecord record = pipelineBiz.copy(
                    param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getToProjectId(), param.getToPipelineName());
            if(null == record){
                throw new Exception("copy pipeline error");
            }

            tranStore.put(TRANSACTION_NEW_PIPELINE_ID, record.getId());
        }, (stepStore, tranStore, retStore, log) -> {
            Long newPipelineId = (Long)tranStore.get(TRANSACTION_NEW_PIPELINE_ID);
            // remove new record
            pipelineBiz.delete(param.getUserId(), param.getToProjectId(), newPipelineId);
        }).addStep("copy-artifact", (stepStore, tranStore, retStore, log) -> {
            // copy artifact
            Long newPipelineId = (Long)tranStore.get(TRANSACTION_NEW_PIPELINE_ID);
            Response copyPipelineFileResp = ServiceRestClient.get(IServiceArtifactRest.class).copyPipelineFile(
                    new ServiceCopyPipelineFileParam(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getToProjectId(), newPipelineId));
            if(!copyPipelineFileResp.isSuccess()){
                logger.error(copyPipelineFileResp.getMessage());
                throw new Exception("copy pipeline file error");
            }

            retStore.put(TRANSACTION_NEW_PIPELINE_ID, newPipelineId);
        }, (stepStore, tranStore, retStore, log) -> {

        });

        Response<Map> resp = pipeline.execute();
        if(!resp.isSuccess()){
            return new Response().error(resp.getMessage());
        }

        Long newPipelineId = (Long)resp.getData().get(TRANSACTION_NEW_PIPELINE_ID);

        return new Response().data(newPipelineId);
    }

    @Override
    public Response getPipelineBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineBasicParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelineBasicParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );

        if(null == record){
            return new Response().error("no pipeline");
        }

        PipelineBasicVo basicVo = new PipelineBasicVo(record);

        return new Response().data(basicVo);
    }

    @Override
    public Response updatePipelineBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineBasicParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineBasicParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = pipelineBiz.update(param.getUserId(), param.getProjectId(), param.getPipelineId(),
                param.getName(), param.getDescription(), param.getFlagOneRun(), param.getFlagInjectProjectVariable(),
                param.getFlagCronEnabled(), param.getCronBeginTime(), param.getCronIntervalSecs(), param.getCronInParams());
        if(!updated){
            return new Response().error("update pipeline basic error");
        }

        return new Response();
    }

    @Override
    public Response updatePipelineBookmark(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineBookmarkParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineBookmarkParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = pipelineBiz.updateBookmark(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getBookmarked());
        if(!updated){
            return new Response().error("update pipeline bookmark error");
        }

        return new Response();
    }

    @Override
    public Response deletePipeline(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelineParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelineParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean delete = pipelineBiz.delete(param.getUserId(), param.getProjectId(), param.getPipelineId());
        if(!delete){
            return new Response().error("delete pipeline error");
        }

        return new Response();
    }

    @Override
    public Response listPipelineVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineVariableParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineVariableParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );

        if(null == record){
            return new Response().error("no pipeline");
        }

        Map<String, PipelineVariableVo> voMap = new LinkedHashMap<>();
        Map<String, PipelineVariable> variables = record.toPojo().getVariables();
        if(null != variables){
            variables.entrySet().stream().forEach(entry -> {
                voMap.put(entry.getKey(), new PipelineVariableVo(entry.getValue()));
            });
        }

        return new Response().data(voMap);
    }

    @Override
    public Response getPipelineVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineVariableParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelineVariableParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );

        if(null == record){
            return new Response().error("no pipeline");
        }

        Pipeline pipeline = record.toPojo();
        Map<String, PipelineVariable> variables = pipeline.getVariables();
        if(null == variables || !variables.containsKey(param.getName())){
            return new Response().error("no variable");
        }

        PipelineVariableVo vo = new PipelineVariableVo(variables.get(param.getName()));

        return new Response().data(vo);
    }

    @Override
    public Response createPipelineVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineVariableParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreatePipelineVariableParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean created = pipelineBiz.createVariable(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getVariable());
        if(!created){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response updatePipelineVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineVariableParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineVariableParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean updated = pipelineBiz.updateVariable(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getVariable());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response deletePipelineVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelineVariableParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelineVariableParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean deleted = pipelineBiz.deleteVariable(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getVariableName());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response listPipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineStepParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId()
        );
        if(null == record){
            return new Response().error("no pipeline");
        }

        Pipeline pipeline = record.toPojo();

        if(null == pipeline.getSteps()){
            return new Response();
        }

        List<PipelineStepVo> stepVos = pipeline.getSteps().stream()
                .map(step -> {
                    PipelineStepTypeRecord typeRecord = pipelineBiz.getStepType(step.getType());

                    PipelineStepVo vo = new PipelineStepVo(
                            step.getType(), typeRecord.getName(), step.getName(), step.getCondition(), step.getDisabled());
                    return vo;
                }).collect(Collectors.toList());


        return new Response().data(stepVos);
    }

    @Override
    public Response getPipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineStepParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRecord record = pipelineBiz.get(param.getUserId(), param.getProjectId(), param.getPipelineId());
        PipelineStep step = record.toPojo().getSteps().get(param.getPosition());
        return new Response().data(step);
    }

    @Override
    public Response addPipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        AddPipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, AddPipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineStep step = param.getStep();
        step.setDisabled(false);
        boolean added = pipelineBiz.addStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPosition(), step);
        if(!added){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response copyPipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CopyPipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CopyPipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean copied = pipelineBiz.copyStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getSrcPosition(), param.getDestPosition());
        if(!copied){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response movePipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        MovePipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, MovePipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean moved = pipelineBiz.moveStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getSrcPosition(), param.getDestPosition());
        if(!moved){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response disablePipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DisablePipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DisablePipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean updateStep = pipelineBiz.disableStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPosition(), param.getDisabled());
        if(!updateStep){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response updatePipelineStepCondition(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineStepConditionParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineStepConditionParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean updateStep = pipelineBiz.updateStepCondition(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPosition(), param.getCondition());
        if(!updateStep){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response updatePipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean updateStep = pipelineBiz.updateStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPosition(), param.getStep());
        if(!updateStep){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response deletePipelineStep(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelineStepParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelineStepParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        boolean updateStep = pipelineBiz.deleteStep(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPosition());
        if(!updateStep){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response listPipelineStepType(Object exc) {
        List<PipelineStepTypeVo> stepTypes = pipelineBiz.listStepType().stream()
                .map(record -> new PipelineStepTypeVo(record)).collect(Collectors.toList());
        return new Response().data(stepTypes);
    }

    @Override
    public Response getPipelineStepType(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineStepType param;
        try {
            param = RestUtil.queryData(exchange, GetPipelineStepType.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineStepTypeRecord record = pipelineBiz.getStepType(param.getType());
        return new Response().data(record.toPojo());
    }

    @Override
    public Response createPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreatePipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // create run record
        PipelineRunRecord pipelineRunRecord = null;
        try {
            pipelineRunRecord = pipelineBiz.createPipelineRun(
                    param.getUserId(), param.getProjectId(), param.getPipelineId(),
                    param.getName(), param.getInParams(), param.getFlagDebug());

            if(null == pipelineRunRecord){
                throw new Exception("create pipeline run error");
            }
        } catch (NoEnoughQuotaException e) {
            Response errResp = new Response();
            errResp.setCode(EnumRespCode.ERROR_NO_ENOUGH_QUOTA);
            errResp.setMessage(e.getMessage());
            return errResp;
        } catch (OnlyOneRunException e) {
            Response errResp = new Response();
            errResp.setCode(EnumRespCode.ERROR_ONLY_ONE_RUN);
            errResp.setMessage(e.getMessage());
            return errResp;
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // create run instance
        Response createRunInstanceResp = ServiceRestClient.get(IServicePipelineRunInstanceRest.class).createPipelineRunInstance(
                new CreatePipelineRunInstanceParam(pipelineRunRecord.getId()));
        if(!createRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunRecord.getId(), EnumPipelineRunStatus.STATUS_FAILED, createRunInstanceResp.getMessage(), null);
            return new Response().error("create pipeline run instance error");
        }

        return new Response().data(pipelineRunRecord.getId());
    }

    @Override
    public Response startPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StartPipelineRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, StartPipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline run
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // start run instance
        Response startRunInstanceResp = ServicePipelineRunInstanceRestClient.get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid()).startPipelineRunInstance(
                new StartPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid(), pipelineRunRecord.getFlagDebug()));
        if(!startRunInstanceResp.isSuccess()){
            pipelineBiz.updatePipelineRunStatus(pipelineRunRecord.getId(), EnumPipelineRunStatus.STATUS_FAILED, startRunInstanceResp.getMessage(), null);
            return new Response().error("start pipeline run instance error");
        }

        return new Response().data(pipelineRunRecord.getId());
    }

    @Override
    public Response forwardPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ForwardPipelineRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, ForwardPipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline run
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // forward run instance
        Response forwardRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .forwardPipelineRunInstance(
                        new ForwardPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid()));
        if(!forwardRunInstanceResp.isSuccess()){
            return new Response().error("forward pipeline run instance error");
        }

        return new Response();
    }

    @Override
    public Response stopPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopPipelineRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, StopPipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline run
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // stop run instance
        Response stopRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .stopPipelineRunInstance(
                        new StopPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid()));
        if(!stopRunInstanceResp.isSuccess()){
            return new Response().error("stop pipeline run instance error");
        }

        return new Response();
    }

    @Override
    public Response deletePipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelineRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // get pipeline run
        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // stop run instance
        Response stopRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServicePipelineRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .stopPipelineRunInstance(
                        new StopPipelineRunInstanceParam(pipelineRunRecord.getInstanceUuid()));
        if(!stopRunInstanceResp.isSuccess()){
            logger.error("stop pipeline run instance error");
        }

        // delete pipeline run record
        boolean delete = pipelineBiz.deletePipelineRun(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId());
        if(!delete){
            return new Response().error("delete pipeline run error");
        }

        return new Response();
    }

    @Override
    public Response listPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineRunParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        // list
        List<PipelineRunBasicVo> list = pipelineBiz.listPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getName(),
                param.getStatus(),
                null,
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> new PipelineRunBasicVo((record))).collect(Collectors.toList());;

        // count
        long count = pipelineBiz.countPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getName(),
                param.getStatus(),
                null
        );

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response getPipelineRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineRunParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelineRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunDetailVo vo = pipelineBiz.getPipelineRunVoDesensitized(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );

        return new Response().data(vo);
    }

    @Override
    public Response createTerminalRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateTerminalRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreateTerminalRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // create terminal run instance
        Response<String> createRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServiceTerminalRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .createTerminalRunInstance(
                        new CreateTerminalRunInstanceParam(pipelineRunRecord.getInstanceUuid()));
        if(!createRunInstanceResp.isSuccess()){
            return new Response().error(createRunInstanceResp.getMessage());
        }

        String terminalRunInstanceUuid = createRunInstanceResp.getData();

        return new Response().data(terminalRunInstanceUuid);
    }

    @Override
    public Response destroyTerminalRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DestroyTerminalRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DestroyTerminalRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // create terminal run instance
        Response destroyRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServiceTerminalRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .destroyTerminalRunInstance(
                        new DestroyTerminalRunInstanceParam(
                                pipelineRunRecord.getInstanceUuid(),
                                param.getTerminalRunInstanceUuid()));
        if(!destroyRunInstanceResp.isSuccess()){
            return new Response().error("destroy terminal run instance error");
        }
        return new Response();
    }

    @Override
    public Response inputTerminalRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        InputTerminalRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, InputTerminalRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // create terminal run instance
        Response inputRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServiceTerminalRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .inputTerminalRunInstance(
                        new InputTerminalRunInstanceParam(
                                pipelineRunRecord.getInstanceUuid(),
                                param.getTerminalRunInstanceUuid(),
                                param.getMessage()));
        if(!inputRunInstanceResp.isSuccess()){
            return new Response().error("input terminal run instance error");
        }
        return new Response();
    }

    @Override
    public Response createExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateExplorerRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreateExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // create explorer run instance
        Response<String> createRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .createExplorerRunInstance(
                        new CreateExplorerRunInstanceParam(pipelineRunRecord.getInstanceUuid()));
        if(!createRunInstanceResp.isSuccess()){
            return new Response().error(createRunInstanceResp.getMessage());
        }

        String explorerRunInstanceUuid = createRunInstanceResp.getData();

        return new Response().data(explorerRunInstanceUuid);
    }

    @Override
    public Response destroyExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DestroyExplorerRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DestroyExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // destroy explorer run instance
        Response destroyRunInstanceResp = ServicePipelineRunInstanceRestClient
                .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .destroyExplorerRunInstance(
                        new DestroyExplorerRunInstanceParam(
                                pipelineRunRecord.getInstanceUuid(),
                                param.getExplorerRunInstanceUuid()));
        if(!destroyRunInstanceResp.isSuccess()){
            return new Response().error("destroy explorer run instance error");
        }
        return new Response();
    }

    @Override
    public Response listFileExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListFileExplorerRunParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListFileExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // create explorer run instance
        Response<List<FsFile>> listFileResp = ServicePipelineRunInstanceRestClient
                .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                .listFileExplorerRunInstance(
                        new ListFileExplorerRunInstanceParam(
                                pipelineRunRecord.getInstanceUuid(),
                                param.getExplorerRunInstanceUuid(),
                                param.getPath()));
        if(!listFileResp.isSuccess()){
            return new Response().error("list file explorer run instance error");
        }

        List<FsFile> list = listFileResp.getData();

        return new Response().data(list);
    }

    @Override
    public void downloadFileExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadFileExplorerRunParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadFileExplorerRunParam.class, true);
        } catch (Exception e) {
            logger.error("download file explorer run error", e);
            return;
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            logger.error("download file explorer run error: no pipeline run");
            return;
        }

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, Paths.get(param.getPath()).getFileName().toString());
        ServicePipelineRunInstanceRestClient
                .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid(), exchange.getOutputStream())
                .downloadFileExplorerRunInstance(
                        new DownloadFileExplorerRunInstanceParam(
                                pipelineRunRecord.getInstanceUuid(),
                                param.getExplorerRunInstanceUuid(),
                                param.getPath()
                        ));
    }

    @Override
    public Response stopDownloadFileExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopDownloadFileExplorerRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, StopDownloadFileExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // stop upload file explorer run instance
        try{
            Response uploadResponse = ServicePipelineRunInstanceRestClient
                    .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                    .stopDownloadFileExplorerRunInstance(
                            new StopDownloadFileExplorerRunInstanceParam(
                                    pipelineRunRecord.getInstanceUuid(),
                                    param.getExplorerRunInstanceUuid(),
                                    param.getPath())
                    );
            if(!uploadResponse.isSuccess()){
                return new Response().error("stop download file explorer run instance error");
            }

            return new Response();
        }catch (Exception e){
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response uploadFileExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadFileExplorerRunParam param;
        try {
            param = EnhancedRestUtil.multiPartFormDataWithHeader(exchange, UploadFileExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // upload file explorer run instance
        try{
            Response uploadResponse = ServicePipelineRunInstanceRestClient
                    .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                    .uploadFileExplorerRunInstance(
                            new UploadFileExplorerRunInstanceParam(
                                    pipelineRunRecord.getInstanceUuid(),
                                    param.getExplorerRunInstanceUuid(),
                                    param.getPath(),
                                    param.getFile().getFileItem().getFileSize(),
                                    param.getFile()));
            if(!uploadResponse.isSuccess()){
                return new Response().error("upload file explorer run instance error");
            }

            return new Response();
        }catch (Exception e){
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response stopUploadFileExplorerRun(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopUploadFileExplorerRunParam param;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, StopUploadFileExplorerRunParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRun(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineRunId()
        );
        if(null == pipelineRunRecord){
            return new Response().error("no pipeline run");
        }

        // stop upload file explorer run instance
        try{
            Response uploadResponse = ServicePipelineRunInstanceRestClient
                    .get(IServiceExplorerRunInstanceRest.class, pipelineRunRecord.getInstanceUuid())
                    .stopUploadFileExplorerRunInstance(
                            new StopUploadFileExplorerRunInstanceParam(
                                    pipelineRunRecord.getInstanceUuid(),
                                    param.getExplorerRunInstanceUuid(),
                                    param.getPath())
                    );
            if(!uploadResponse.isSuccess()){
                return new Response().error("stop upload file explorer run instance error");
            }

            return new Response();
        }catch (Exception e){
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response createPipelineWebhookTrigger(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreatePipelineWebhookTriggerParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreatePipelineWebhookTriggerParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        PipelineWebhookTriggerRecord record = pipelineBiz.createPipelineWebhookTrigger(param.getUserId(), param.getProjectId(), param.getPipelineId(),
                param.getSourceType(), param.getEventType(), param.getTag(), param.getInParams());
        if(null == record){
            return new Response().error();
        }

        return new Response().data(record.getId());
    }

    @Override
    public Response updatePipelineWebhookTrigger(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UpdatePipelineWebhookTriggerParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdatePipelineWebhookTriggerParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = pipelineBiz.updatePipelineWebhookTrigger(param.getUserId(), param.getProjectId(), param.getPipelineId(),
                param.getPipelineWebhookTriggerId(), param.getSourceType(), param.getEventType(), param.getTag(), param.getInParams());
        if(!updated){
            return new Response().error("update pipeline webhook trigger error");
        }

        return new Response();
    }

    @Override
    public Response deletePipelineWebhookTrigger(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelineWebhookTriggerParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelineWebhookTriggerParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean delete = pipelineBiz.deletePipelineWebhookTrigger(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineWebhookTriggerId());
        if(!delete){
            return new Response().error("delete pipeline webhook trigger error");
        }

        return new Response();
    }

    @Override
    public Response getPipelineWebhookTrigger(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelineWebhookTriggerParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelineWebhookTriggerParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelineWebhookTriggerRecord record = pipelineBiz.getPipelineWebhookTrigger(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelineWebhookTriggerId()
        );

        if(null == record){
            return new Response().error("no pipeline webhook trigger");
        }

        return new Response().data(record.toPojo());
    }

    @Override
    public Response listPipelineWebhookTrigger(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelineWebhookTriggerParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelineWebhookTriggerParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<PipelineWebhookTrigger> list = pipelineBiz.listPipelineWebhookTrigger(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> record.toPojo()).collect(Collectors.toList());

        long count = pipelineBiz.countPipelineWebhookTrigger(param.getUserId(), param.getProjectId(), param.getPipelineId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response listPipelinePending(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListPipelinePendingParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListPipelinePendingParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<PipelinePending> list = pipelineBiz.listPipelinePending(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> record.toPojo()).collect(Collectors.toList());

        long count = pipelineBiz.countPipelinePending(param.getUserId(), param.getProjectId(), param.getPipelineId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }

    @Override
    public Response getPipelinePending(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        GetPipelinePendingParam param;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetPipelinePendingParam.class, true);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        PipelinePendingRecord record = pipelineBiz.getPipelinePending(
                param.getUserId(),
                param.getProjectId(),
                param.getPipelineId(),
                param.getPipelinePendingId()
        );

        if(null == record){
            return new Response().error("no pipeline pending");
        }

        return new Response().data(record.toPojo());
    }

    @Override
    public Response deletePipelinePending(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DeletePipelinePendingParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeletePipelinePendingParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean delete = pipelineBiz.deletePipelinePending(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelinePendingId());
        if(!delete){
            return new Response().error("delete pipeline pending error");
        }

        return new Response();
    }

}
