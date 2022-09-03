package com.airxiechao.y20.project.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineCountVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineVariableVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCountPipelineParam;
import com.airxiechao.y20.project.biz.api.IProjectBiz;
import com.airxiechao.y20.project.db.record.ProjectRecord;
import com.airxiechao.y20.project.pojo.vo.ProjectBasicVo;
import com.airxiechao.y20.project.pojo.vo.ProjectDetailVo;
import com.airxiechao.y20.project.rest.param.*;
import com.airxiechao.y20.project.rest.api.IUserProjectRest;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserProjectRestHandler implements IUserProjectRest {

    private static final Logger logger = LoggerFactory.getLogger(UserProjectRestHandler.class);
    private IProjectBiz projectBiz = Biz.get(IProjectBiz.class);

    @Override
    public Response list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListProjectParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListProjectParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // count
        long count = projectBiz.count(param.getUserId(), param.getName());

        // list
        List<ProjectBasicVo> list = projectBiz.list(
                param.getUserId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> new ProjectBasicVo(record)).collect(Collectors.toList());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response listDetail(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListProjectParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListProjectParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // count
        long count = projectBiz.count(param.getUserId(), param.getName());

        // list
        List<ProjectDetailVo> list = projectBiz.list(
                param.getUserId(),
                param.getName(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize()
        ).stream().map(record -> {
            PipelineCountVo pipelineCountVo;
            Response<PipelineCountVo> countPipelineResp = ServiceRestClient.get(IServicePipelineRest.class).countPipeline(
                    new ServiceCountPipelineParam(record.getUserId(), record.getId())
            );
            if(countPipelineResp.isSuccess()){
                pipelineCountVo = countPipelineResp.getData();
                return new ProjectDetailVo(record, pipelineCountVo.getTotal(), pipelineCountVo.getRunning());
            }

            return new ProjectDetailVo(record, null, null);
        }).collect(Collectors.toList());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list
        ));
    }

    @Override
    public Response getBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetProjectBasicParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetProjectBasicParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        ProjectRecord record = projectBiz.getById(param.getUserId(), param.getProjectId());
        return new Response().data(new ProjectBasicVo(record));
    }

    @Override
    public Response updateBasic(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateProjectBasicParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateProjectBasicParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = projectBiz.updateBasic(param.getUserId(), param.getProjectId(), param.getName());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response updateBookmark(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateProjectBookmarkParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateProjectBookmarkParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = projectBiz.updateBookmark(param.getUserId(), param.getProjectId(), param.getBookmarked());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response create(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateProjectParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreateProjectParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        ProjectRecord record = projectBiz.create(param.getUserId(), param.getName());
        if(null == record){
            return new Response().error();
        }

        return new Response().data(record.getId());
    }

    @Override
    public Response delete(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteProjectParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeleteProjectParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean deleted = projectBiz.delete(param.getUserId(), param.getProjectId());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response listVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListProjectVariableParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListProjectVariableParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        ProjectRecord record = projectBiz.getById(param.getUserId(), param.getProjectId());
        Map<String, PipelineVariableVo> voMap = new LinkedHashMap<>();
        Map<String, PipelineVariable> variables = record.toPojo().getVariables();
        if(null != variables) {
            variables.entrySet().stream().forEach(entry -> {
                voMap.put(entry.getKey(), new PipelineVariableVo(entry.getValue()));
            });
        }

        return new Response().data(voMap);
    }

    @Override
    public Response getVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetProjectVariableParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetProjectVariableParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        ProjectRecord record = projectBiz.getById(param.getUserId(), param.getProjectId());
        Map<String, PipelineVariable> projectVariables = record.toPojo().getVariables();
        if(null == projectVariables){
            return null;
        }

        PipelineVariable variable = projectVariables.get(param.getName());
        PipelineVariableVo vo = new PipelineVariableVo(variable);

        return new Response().data(vo);
    }

    @Override
    public Response createVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        CreateProjectVariableParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, CreateProjectVariableParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean created = projectBiz.createVariable(param.getUserId(), param.getProjectId(), param.getVariable());
        if(!created){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response updateVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        UpdateProjectVariableParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, UpdateProjectVariableParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean updated = projectBiz.updateVariable(param.getUserId(), param.getProjectId(), param.getVariable());
        if(!updated){
            return new Response().error();
        }

        return new Response();
    }

    @Override
    public Response deleteVariable(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        DeleteProjectVariableParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, DeleteProjectVariableParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        boolean deleted = projectBiz.deleteVariable(param.getUserId(), param.getProjectId(), param.getVariableName());
        if(!deleted){
            return new Response().error();
        }

        return new Response();
    }


}
