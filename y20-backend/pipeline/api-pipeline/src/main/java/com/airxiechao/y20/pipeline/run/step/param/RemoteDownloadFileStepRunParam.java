package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class RemoteDownloadFileStepRunParam {

    @StepTypeParam(
            displayName = "源路径",
            hint = "服务端文件或目录的绝对路径。可以通过上传文件自动获取路径",
            type = EnumPipelineParamType.FILE)
    @Required
    private String srcPath;

    @StepTypeParam(
            displayName = "目的路径",
            hint = "本地节点文件或目录的绝对路径，或者相对当前工作目录的相对路径",
            defaultValue = "./",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String dstPath;

    public RemoteDownloadFileStepRunParam(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getDstPath() {
        return dstPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }
}
