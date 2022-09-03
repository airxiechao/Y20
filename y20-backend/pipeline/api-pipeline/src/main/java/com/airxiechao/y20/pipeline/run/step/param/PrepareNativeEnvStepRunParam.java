package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

import java.util.Map;

public class PrepareNativeEnvStepRunParam {

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

    public PrepareNativeEnvStepRunParam(Map<String, String> env,
                                        String cacheDirs, String artifactProjectFileDir, String artifactPipelineFileDir) {
        this.env = env;
        this.cacheDirs = cacheDirs;
        this.artifactProjectFileDir = artifactProjectFileDir;
        this.artifactPipelineFileDir = artifactPipelineFileDir;
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
