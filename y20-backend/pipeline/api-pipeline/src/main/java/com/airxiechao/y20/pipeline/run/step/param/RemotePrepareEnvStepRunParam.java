package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

import java.util.Map;

public class RemotePrepareEnvStepRunParam {

    @StepTypeParam(
            displayName = "节点",
            type = EnumPipelineParamType.SELECT_AGENT)
    @Required
    private String agentId;

    @StepTypeParam(
            displayName = "镜像",
            type = EnumPipelineParamType.INPUT)
    private String image;

    @StepTypeParam(
            displayName = "镜像仓库地址",
            type = EnumPipelineParamType.INPUT)
    private String imageServerAddress;

    @StepTypeParam(
            displayName = "镜像仓库用户名",
            type = EnumPipelineParamType.INPUT)
    private String imageServerUsername;

    @StepTypeParam(
            displayName = "镜像仓库密码",
            type = EnumPipelineParamType.PASSWORD)
    private String imageServerPassword;

    @StepTypeParam(
            displayName = "自动注入变量到环境变量",
            type = EnumPipelineParamType.CHECKBOX)
    private Boolean injectVariableIntoEnv;

    @StepTypeParam(
            displayName = "环境变量",
            type = EnumPipelineParamType.INPUT)
    private Map<String, String> env;

    @StepTypeParam(
            displayName = "持久化目录",
            type = EnumPipelineParamType.INPUT)
    private String cacheDirs;

    @StepTypeParam(
            displayName = "同步项目文件到工作目录",
            type = EnumPipelineParamType.CHECKBOX)
    private Boolean syncProjectFile;

    @StepTypeParam(
            displayName = "同步流水线文件到工作目录",
            type = EnumPipelineParamType.CHECKBOX)
    private Boolean syncPipelineFile;

    public RemotePrepareEnvStepRunParam(String agentId, String image, String imageServerAddress,
                                        String imageServerUsername, String imageServerPassword,
                                        Map<String, String> env, Boolean injectVariableIntoEnv,
                                        String cacheDirs, Boolean syncProjectFile, Boolean syncPipelineFile) {
        this.agentId = agentId;
        this.image = image;
        this.imageServerAddress = imageServerAddress;
        this.imageServerUsername = imageServerUsername;
        this.imageServerPassword = imageServerPassword;
        this.env = env;
        this.injectVariableIntoEnv = injectVariableIntoEnv;
        this.cacheDirs = cacheDirs;
        this.syncProjectFile = syncProjectFile;
        this.syncPipelineFile = syncPipelineFile;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageServerAddress() {
        return imageServerAddress;
    }

    public void setImageServerAddress(String imageServerAddress) {
        this.imageServerAddress = imageServerAddress;
    }

    public String getImageServerUsername() {
        return imageServerUsername;
    }

    public void setImageServerUsername(String imageServerUsername) {
        this.imageServerUsername = imageServerUsername;
    }

    public String getImageServerPassword() {
        return imageServerPassword;
    }

    public void setImageServerPassword(String imageServerPassword) {
        this.imageServerPassword = imageServerPassword;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public Boolean getInjectVariableIntoEnv() {
        return injectVariableIntoEnv;
    }

    public void setInjectVariableIntoEnv(Boolean injectVariableIntoEnv) {
        this.injectVariableIntoEnv = injectVariableIntoEnv;
    }

    public String getCacheDirs() {
        return cacheDirs;
    }

    public void setCacheDirs(String cacheDirs) {
        this.cacheDirs = cacheDirs;
    }

    public Boolean getSyncProjectFile() {
        return syncProjectFile;
    }

    public void setSyncProjectFile(Boolean syncProjectFile) {
        this.syncProjectFile = syncProjectFile;
    }

    public Boolean getSyncPipelineFile() {
        return syncPipelineFile;
    }

    public void setSyncPipelineFile(Boolean syncPipelineFile) {
        this.syncPipelineFile = syncPipelineFile;
    }
}
