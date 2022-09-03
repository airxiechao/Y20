package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

import java.util.Map;

public class PrepareDockerEnvStepRunParam {

    @StepTypeParam(
            displayName = "镜像",
            type = EnumPipelineParamType.INPUT)
    @Required
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
            displayName = "环境变量",
            type = EnumPipelineParamType.INPUT)
    private Map<String, String> env;

    @StepTypeParam(
            displayName = "持久化目录",
            type = EnumPipelineParamType.INPUT)
    private String cacheDirs;

    @StepTypeParam(
            displayName = "同步项目文件目录",
            type = EnumPipelineParamType.INPUT)
    private String artifactProjectFileDir;

    @StepTypeParam(
            displayName = "同步流水线文件目录",
            type = EnumPipelineParamType.INPUT)
    private String artifactPipelineFileDir;

    public PrepareDockerEnvStepRunParam(
            String image, String imageServerAddress, String imageServerUsername, String imageServerPassword,
            Map<String, String> env,
            String cacheDirs, String artifactProjectFileDir, String artifactPipelineFileDir) {
        this.image = image;
        this.imageServerAddress = imageServerAddress;
        this.imageServerUsername = imageServerUsername;
        this.imageServerPassword = imageServerPassword;
        this.env = env;
        this.cacheDirs = cacheDirs;
        this.artifactProjectFileDir = artifactProjectFileDir;
        this.artifactPipelineFileDir = artifactPipelineFileDir;
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

    public String getCacheDirs() {
        return cacheDirs;
    }

    public void setCacheDirs(String cacheDirs) {
        this.cacheDirs = cacheDirs;
    }

    public String getArtifactProjectFileDir() {
        return artifactProjectFileDir;
    }

    public void setArtifactProjectFileDir(String artifactProjectFileDir) {
        this.artifactProjectFileDir = artifactProjectFileDir;
    }

    public String getArtifactPipelineFileDir() {
        return artifactPipelineFileDir;
    }

    public void setArtifactPipelineFileDir(String artifactPipelineFileDir) {
        this.artifactPipelineFileDir = artifactPipelineFileDir;
    }
}
