package com.airxiechao.y20.pipeline.run.step.file;

import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunCategory;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.*;

@StepRun(
        visible = true,
        order = 2,
        type = EnumStepRunType.REMOTE_DOWNLOAD_FILE_TYPE,
        name = EnumStepRunType.REMOTE_DOWNLOAD_FILE_NAME,
        category = EnumStepRunCategory.FILE,
        icon = "download",
        description = "将服务器的项目文件或流水线文件下载到本地节点",
        paramClass = RemoteDownloadFileStepRunParam.class)
public class RemoteDownloadFileStepRun extends RemoteStepRunInstance {

    private RemoteDownloadFileStepRunParam param;

    public RemoteDownloadFileStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteDownloadFileStepRunParam.class);

        PipelineStep fileStep = new PipelineStep();
        fileStep.setType(EnumStepRunType.PULL_ARTIFACT_FILE_TYPE);
        if(StringUtil.isBlank(this.env.getDockerContainerId())){
            fileStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PullArtifactFileStepRunParam(
                    param.getSrcPath(),
                    param.getDstPath(),
                    env.getWorkingDir()
            )));
        }else{
            fileStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PullArtifactFileStepRunParam(
                    param.getSrcPath(),
                    param.getDstPath(),
                    env.getDockerHostWorkingDir()
            )));
        }


        this.step = fileStep;
    }


}