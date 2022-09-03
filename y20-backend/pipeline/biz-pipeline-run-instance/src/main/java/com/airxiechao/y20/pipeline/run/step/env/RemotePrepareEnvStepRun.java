package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.artifact.ArtifactUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.biz.api.IPipelineBiz;
import com.airxiechao.y20.pipeline.db.record.PipelineRunRecord;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunCategory;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.RemoteStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.PrepareDockerEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.PrepareNativeEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.step.param.RemotePrepareEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.PipelineStepRunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@StepRun(
        order = 1,
        visible = true,
        type = EnumStepRunType.REMOTE_PREPARE_ENV_TYPE,
        name = EnumStepRunType.REMOTE_PREPARE_ENV_NAME,
        category = EnumStepRunCategory.ENV,
        icon = "computer",
        description = "配置流水线其他步骤的执行节点和环境参数。用于设置流水线的启动环境或者中途切换环境",
        paramClass = RemotePrepareEnvStepRunParam.class)
public class RemotePrepareEnvStepRun extends RemoteStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(RemotePrepareEnvStepRun.class);

    private IPipelineBiz pipelineBiz = Biz.get(IPipelineBiz.class);

    private RemotePrepareEnvStepRunParam param;

    // env must only contains userId
    public RemotePrepareEnvStepRun(String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        super(stepRunInstanceUuid, env, pipelineRunContext);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), RemotePrepareEnvStepRunParam.class);

        this.env.setAgentId(param.getAgentId());

        PipelineRunRecord pipelineRunRecord = pipelineBiz.getPipelineRunByInstanceUuid(this.env.getPipelineRunInstanceUuid());
        if(null == pipelineRunRecord){
            logger.error("assemble remote-prepare-env-step-run error: no pipeline run record [{}]", this.env.getPipelineRunInstanceUuid());
            throw new RuntimeException("no pipeline run record");
        }

        String artifactProjectFileDir = null;
        String artifactPipelineFileDir = null;

        if(param.getSyncProjectFile()){
            artifactProjectFileDir = ArtifactUtil.getProjectFilePath(
                    env.getUserId(), pipelineRunRecord.getProjectId(), null, null
            );
        }
        if(param.getSyncPipelineFile()){
            artifactPipelineFileDir = ArtifactUtil.getPipelineFilePath(
                    env.getUserId(), pipelineRunRecord.getProjectId(), pipelineRunRecord.getPipelineId(), null, null
            );
        }

        // env variables
        Map<String, String> envVariables = param.getEnv();
        Boolean injectVariableIntoEnv = param.getInjectVariableIntoEnv();
        if(null != injectVariableIntoEnv && injectVariableIntoEnv){
            pipelineRunContext.getVariableNames().forEach(name -> {
                envVariables.put(name, pipelineRunContext.getVariableActualValue(name));
            });
        }
        PipelineStepRunUtil.replaceHolderByContext(envVariables, pipelineRunContext);

        PipelineStep prepareStep = new PipelineStep();
        if(!StringUtil.isBlank(param.getImage())){
            prepareStep.setType(EnumStepRunType.PREPARE_DOCKER_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PrepareDockerEnvStepRunParam(
                    param.getImage(), param.getImageServerAddress(), param.getImageServerUsername(), param.getImageServerPassword(),
                    envVariables,
                    param.getCacheDirs(), artifactProjectFileDir, artifactPipelineFileDir
            )));
        }else{
            prepareStep.setType(EnumStepRunType.PREPARE_NATIVE_ENV_TYPE);
            prepareStep.setParams(ModelUtil.toMapAndCheckRequiredField(new PrepareNativeEnvStepRunParam(
                    envVariables, param.getCacheDirs(), artifactProjectFileDir, artifactPipelineFileDir
            )));
        }

        this.step = prepareStep;
    }

}
