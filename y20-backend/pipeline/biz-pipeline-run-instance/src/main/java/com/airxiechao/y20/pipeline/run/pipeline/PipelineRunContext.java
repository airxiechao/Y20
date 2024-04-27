package com.airxiechao.y20.pipeline.run.pipeline;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.crypto.AesUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.config.CommonConfig;
import com.airxiechao.y20.common.pojo.config.VariableCommonConfig;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;
import com.airxiechao.y20.pipeline.pojo.PipelineVariable;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineVariableKind;
import com.airxiechao.y20.project.biz.api.IProjectBiz;
import com.airxiechao.y20.project.pojo.Project;
import com.airxiechao.y20.project.rest.api.IServiceProjectRest;
import com.airxiechao.y20.project.rest.param.GetProjectBasicParam;

public class PipelineRunContext extends AbstractPipelineRunContext {

    private static final VariableCommonConfig variableCommonConfig = ConfigFactory.get(CommonConfig.class).getVariable();

    private IProjectBiz projectBiz = Biz.get(IProjectBiz.class);

    public PipelineRunContext(PipelineRun pipelineRun) {
        Response<Project> projectResp = ServiceRestClient.get(IServiceProjectRest.class).get(
                new GetProjectBasicParam(pipelineRun.getUserId(), pipelineRun.getProjectId()));

        projectVariables = projectResp.getData().getVariables();
        Boolean flagInjectProjectVariable = pipelineRun.getPipeline().getFlagInjectProjectVariable();
        if(null != flagInjectProjectVariable && flagInjectProjectVariable && null != projectVariables){
            // inject project variable
            pipelineVariables.putAll(projectVariables);
        }

        pipelineVariables.putAll(pipelineRun.getPipeline().getVariables());
        pipelineRunInParams = pipelineRun.getInParams();
    }

    @Override
    public String getVariableActualValue(String name){
        PipelineVariable variable = this.pipelineVariables.get(name);
        if(null == variable){
            return null;
        }

        switch (variable.getKind()){
            case EnumPipelineVariableKind.IN:
                String inName = variable.getName();
                if(null == pipelineRunInParams || !pipelineRunInParams.containsKey(inName)){
                    return null;
                }
                String inParamValue = pipelineRunInParams.get(inName);
                return inParamValue;
            case EnumPipelineVariableKind.SECRET:
                try {
                    return AesUtil.decryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(), variable.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            case EnumPipelineVariableKind.PROJECT_VARIABLE_REFERENCE:
                String referenceName = variable.getValue();
                if(null == projectVariables || !projectVariables.containsKey(referenceName)){
                    return null;
                }

                PipelineVariable projectVariable = projectVariables.get(referenceName);
                if(EnumPipelineVariableKind.SECRET.equals(projectVariable.getKind())){
                    try {
                        return AesUtil.decryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(), projectVariable.getValue());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    return projectVariable.getValue();
                }

            default:
                return variable.getValue();
        }
    }

    @Override
    public void setVariableValue(String name, String value){
        PipelineVariable variable = this.pipelineVariables.get(name);
        if(null == variable){
            variable = new PipelineVariable();
            variable.setKind(EnumPipelineVariableKind.STEP_OUTPUT);
            variable.setParamType(EnumPipelineParamType.INPUT);
            variable.setName(name);
            this.pipelineVariables.put(name, variable);
        }

        String kind = variable.getKind();
        if(!StringUtil.isBlank(kind)){
            switch (kind){
                case EnumPipelineVariableKind.SECRET:
                    try {
                        variable.setValue(AesUtil.encryptByPBKDF2(variableCommonConfig.getSecretVariableEncryptKey(), variableCommonConfig.getSecretVariableEncryptKey(),value));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    variable.setValue(value);
                    break;
            }
        }else{
            variable.setValue(value);
        }
    }
}
