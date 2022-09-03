package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumArtifactFileDestDir;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeSelectOption;

public class RemoteUploadFileStepRunParam {

    @StepTypeParam(
            displayOrder = 1,
            displayName = "源路径",
            hint = "本地节点文件或目录的绝对路径，或者相对当前工作目录的相对路径",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String srcPath;

    @StepTypeParam(
            displayOrder = 2,
            displayName = "目的所属目录",
            hint = "服务端目的文件的所属目录",
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
            displayOrder = 3,
            displayName = "目的路径",
            hint = "服务端目的文件相对“目的所属目录”的相对路径",
            defaultValue = "./",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String dstPath;

    @StepTypeParam(
            displayOrder = 4,
            displayName = "目的所属目录的输出变量",
            hint = "服务端目的文件的所属目录路径，将通过该变量输出。可用来拼接出目的文件完整路径",
            type = EnumPipelineParamType.INPUT)
    private String output;

    public RemoteUploadFileStepRunParam(String srcPath, String destDir, String dstPath, String output) {
        this.srcPath = srcPath;
        this.destDir = destDir;
        this.dstPath = dstPath;
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

    public String getDstPath() {
        return dstPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
