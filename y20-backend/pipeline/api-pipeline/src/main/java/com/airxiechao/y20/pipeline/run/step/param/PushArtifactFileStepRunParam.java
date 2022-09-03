package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumArtifactFileDestDir;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;

public class PushArtifactFileStepRunParam {
    @StepTypeParam(
            displayName = "源路径",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String srcPath;

    @StepTypeParam(
            displayName = "目的所属目录",
            type = EnumPipelineParamType.SELECT,
            options = {
                    @StepTypeSelectOption(value = EnumArtifactFileDestDir.PIPELINE_RUN, label = EnumArtifactFileDestDir.PIPELINE_RUN_LABEL),
                    @StepTypeSelectOption(value = EnumArtifactFileDestDir.PIPELINE, label = EnumArtifactFileDestDir.PIPELINE_LABEL),
                    @StepTypeSelectOption(value = EnumArtifactFileDestDir.PROJECT, label = EnumArtifactFileDestDir.PROJECT_LABEL),
            }
    )
    @Required
    private String destDir;

    @StepTypeParam(
            displayName = "目的相对路径",
            type = EnumPipelineParamType.INPUT)
    @Required()
    private String destPath;

    @StepTypeParam(
            displayName = "工作目录",
            type = EnumPipelineParamType.INPUT)
    private String workingDir;

    @StepTypeParam(
            displayName = "目的所属目录的输出变量名",
            type = EnumPipelineParamType.INPUT)
    private String output;

    public PushArtifactFileStepRunParam(String srcPath, String destDir, String destPath, String workingDir, String output) {
        this.srcPath = srcPath;
        this.destDir = destDir;
        this.destPath = destPath;
        this.workingDir = workingDir;
        this.output = output;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDestDir() {
        return destDir;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
