package com.airxiechao.y20.project.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.project.biz.api.IProjectBiz;
import com.airxiechao.y20.project.db.record.ProjectRecord;
import com.airxiechao.y20.project.pojo.Project;
import com.airxiechao.y20.project.rest.api.IServiceProjectRest;
import com.airxiechao.y20.project.rest.param.GetProjectBasicParam;
import com.airxiechao.y20.project.rest.param.ServiceExistProjectParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceProjectRestHandler implements IServiceProjectRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProjectRestHandler.class);
    private IProjectBiz projectBiz = Biz.get(IProjectBiz.class);

    @Override
    public Response<Project> get(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetProjectBasicParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetProjectBasicParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        if(param.getProjectId() != 0) {
            ProjectRecord record = projectBiz.getById(param.getUserId(), param.getProjectId());
            if (null == record) {
                return new Response().error("no project");
            }

            return new Response().data(record.toPojo());
        }else{
            Project emptyProject = new Project();
            emptyProject.setProjectId(0L);
            return new Response().data(emptyProject);
        }
    }

    @Override
    public Response exist(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ServiceExistProjectParam param = null;
        try {
            param = RestUtil.queryData(exchange, ServiceExistProjectParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        ProjectRecord record = projectBiz.getById(param.getUserId(), param.getProjectId());
        if(null == record){
            return new Response().error();
        }

        return new Response();
    }
}
