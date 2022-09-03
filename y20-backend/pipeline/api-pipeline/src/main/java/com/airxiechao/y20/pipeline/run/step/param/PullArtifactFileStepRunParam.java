package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class PullArtifactFileStepRunParam {

    @StepTypeParam(
            displayName = "源路径",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String srcPath;

    @StepTypeParam(
            displayName = "目的路径",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String destPath;

    @StepTypeParam(
            displayName = "工作目录",
            type = EnumPipelineParamType.INPUT)
    private String workingDir;

    public PullArtifactFileStepRunParam(String srcPath, String destPath, String workingDir) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.workingDir = workingDir;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}