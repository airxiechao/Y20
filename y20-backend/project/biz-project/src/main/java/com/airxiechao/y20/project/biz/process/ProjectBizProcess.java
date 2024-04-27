package com.airxiechao.y20.project.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.VariableCommonConfig;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineVariableKind;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineVariableVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceDeletePipelineParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceDeleteProjectAllPipelineParam;
import com.airxiechao.y20.project.biz.api.IProjectBiz;
import com.airxiechao.y20.project.db.api.IProjectDb;
import com.airxiechao.y20.project.db.record.ProjectRecord;
import com.airxiechao.y20.project.pojo.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectBizProcess implements IProjectBiz {

    private static final Logger logger = LoggerFactory.getLogger(ProjectBizProcess.class);
    private IProjectDb projectDb = Db.get(IProjectDb.class);

    private VariableCommonConfig variableCommonConfig = ConfigFactory.get(CommonConfig.class).getVariable();

    @Override
    public List<ProjectRecord> list(Long userId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return projectDb.list(userId, name, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long count(Long userId, String name) {
        return projectDb.count(userId, name);
    }

    @Override
    public ProjectRecord getById(Long userId, Long projectId) {
        return projectDb.getById(userId, projectId);
    }

    @Override
    public ProjectRecord create(Long userId, String name) {
        ProjectRecord record = new ProjectRecord();
        record.setUserId(userId);
        record.setName(name);
        record.setBookmarked(false);
        record.setDeleted(false);
        record.setCreateTime(new Date());
        boolean created = projectDb.create(record);
        if(!created){
            return null;
        }

        return record;
    }

    @Override
    public boolean delete(Long userId, Long projectId) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        record.setDeleted(true);
        record.setDeleteTime(new Date());

        boolean deleted = projectDb.update(record);
        if(!deleted){
            return false;
        }

        // delete pipeline
        Response deletePipelineResp = ServiceRestClient.get(IServicePipelineRest.class).deleteProjectAllPipeline(
                new ServiceDeleteProjectAllPipelineParam(projectId));
        if(!deletePipelineResp.isSuccess()){
            logger.error("delete project [{}] pipeline error: {}", projectId, deletePipelineResp.getMessage());
        }

        return true;
    }

    @Override
    public boolean updateBasic(Long userId, Long projectId, String name) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        record.setName(name);

        return projectDb.update(record);
    }

    @Override
    public boolean updateBookmark(Long userId, Long projectId, Boolean bookmarked) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        record.setBookmarked(bookmarked);
        if(bookmarked){
            record.setBookmarkTime(new Date());
        }else{
            record.setBookmarkTime(null);
        }

        return projectDb.update(record);
    }

    @Override
    public boolean createVariable(Long userId, Long projectId, PipelineVariable variable) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        Project project = record.toPojo();
        Map<String, PipelineVariable> variables = project.getVariables();
        if(null == variables){
            variables = new HashMap<>();
            project.setVariables(variables);
        }

        String name = variable.getName();
        if(variables.containsKey(name)){
            throw new RuntimeException("variable already exists");
        }

        if(EnumPipelineVariableKind.SECRET.equals(variable.getKind())){
            try {
                String value = AesUtil.encryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(),variable.getValue());
                variable.setValue(value);
                variables.put(name, variable);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            variables.put(name, variable);
        }

        return projectDb.update(project.toRecord());
    }

    @Override
    public boolean updateVariable(Long userId, Long projectId, PipelineVariable variable) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        Project project = record.toPojo();
        Map<String, PipelineVariable> variables = project.getVariables();
        if(null == variables){
            variables = new HashMap<>();
            project.setVariables(variables);
        }

        String name = variable.getName();
        if(!variables.containsKey(name)){
            throw new RuntimeException("no variable");
        }

        if(EnumPipelineVariableKind.SECRET.equals(variable.getKind())){
            try {
                String value = variable.getValue();
                if(!PipelineVariableVo.SECRET_MASK.equals(value)) {
                    value = AesUtil.encryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(), value);
                }else{
                    value = variables.get(name).getValue();
                }
                variable.setValue(value);
                variables.put(name, variable);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            variables.put(name, variable);
        }

        return projectDb.update(project.toRecord());
    }

    @Override
    public boolean deleteVariable(Long userId, Long projectId, String variableName) {
        ProjectRecord record = projectDb.getById(userId, projectId);
        if(null == record){
            throw new RuntimeException("no project");
        }

        Project project = record.toPojo();
        Map<String, PipelineVariable> variables = project.getVariables();
        if(null == variables || !variables.containsKey(variableName)){
            throw new RuntimeException("no variable");
        }

        variables.remove(variableName);

        return projectDb.update(project.toRecord());
    }
}
