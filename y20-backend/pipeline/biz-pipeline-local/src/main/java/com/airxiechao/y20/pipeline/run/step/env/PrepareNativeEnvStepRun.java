package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.PrepareNativeEnvStepRunParam;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;

@StepRun(
        type = EnumStepRunType.PREPARE_NATIVE_ENV_TYPE,
        name = EnumStepRunType.PREPARE_NATIVE_ENV_NAME,
        paramClass = PrepareNativeEnvStepRunParam.class)
public class PrepareNativeEnvStepRun extends AbstractPrepareEnvStepRun {

    private static final Logger logger = LoggerFactory.getLogger(PrepareNativeEnvStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private PrepareNativeEnvStepRunParam param;

    public PrepareNativeEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), PrepareNativeEnvStepRunParam.class);
    }

    @Override
    protected Response run() throws Exception {
        appendLogLine(String.format("prepare native env on agent [%s]", agentClientConfig.getAgentId()));

        String envInstanceUuid = getStepRunInstanceUuid();
        String workingDir = Paths.get(
                agentClientConfig.getDataDir(),
                "workspace",
                envInstanceUuid).toFile().getCanonicalFile().toString();

        FileUtil.mkDirs(workingDir);

        String cacheDir = Paths.get(agentClientConfig.getDataDir(), "cache").toFile().getCanonicalFile().toString();
        FileUtil.mkDirs(cacheDir);

        // export env variables to file (.env)
        Map<String, String> envVariables = param.getEnv();
        if(null != envVariables){
            LocalStepRunUtil.writeEnvVariablesToFile(envVariables, workingDir);
        }

        // link cache dirs
        if(!StringUtil.isBlank(param.getCacheDirs())){
            linkNativeCacheDirs(param.getCacheDirs(), cacheDir, workingDir);
        }

        // sync artifact project file
        if(!StringUtil.isBlank(param.getArtifactProjectFileDir())){
            appendLogLine("sync artifact project file");
            syncArtifactFile(param.getArtifactProjectFileDir(), workingDir);
        }

        // sync artifact pipeline file
        if(!StringUtil.isBlank(param.getArtifactPipelineFileDir())){
            appendLogLine("sync artifact pipeline file");
            syncArtifactFile(param.getArtifactPipelineFileDir(), workingDir);
        }

        appendLogLine("native env ready");

        Env env = new Env();
        env.setWorkingDir(workingDir);
        env.setEnvInstanceUuid(envInstanceUuid);
        return new Response().data(env);
    }
}
