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
        order = 1,
        type = EnumStepRunType.REMOTE_UPLOAD_FILE_TYPE,
        name = EnumStepRunType.REMOTE_UPLOAD_FILE_NAME,
        category = EnumStepRunCategory.FILE,
        icon = "upload",
        description = "将本地节点的文件上传到服务器的项目文件或流水线文件",
        paramClass = RemoteUploadFileStepRunParam.class,
        outputs = { "\"目的所属目录的输出变量\"参数中的变量" }
)
public class RemoteUploadFileStepRun extends RemoteStepRunInstance {

    private RemoteUploadFileStepRunParam param;

    public RemoteUploadFileStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemoteUploadFileStepRunParam.class);

        PipelineStep fileStep = new PipelineStep();
        fileStep.setType(EnumStepRunType.PUSH_ARTIFACT_FILE_TYPE);
        if(StringUtil.isBlank(this.env.getDockerContainerId())){
            fileStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PushArtifactFileStepRunParam(
                    param.getSrcPath(),
                    param.getDestDir(),
                    param.getDstPath(),
                    env.getWorkingDir(),
                    param.getOutput()
            )));
        }else{
            fileStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PushArtifactFileStepRunParam(
                    param.getSrcPath(),
                    param.getDestDir(),
                    param.getDstPath(),
                    env.getDockerHostWorkingDir(),
                    param.getOutput()
            )));
        }


        this.step = fileStep;
    }


}