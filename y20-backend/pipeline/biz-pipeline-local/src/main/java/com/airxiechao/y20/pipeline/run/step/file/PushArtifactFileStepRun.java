package com.airxiechao.y20.pipeline.run.step.file;

import com.airxiechao.axcboot.communication.common.FileData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.artifact.rest.api.IAgentArtifactRest;
import com.airxiechao.y20.artifact.rest.param.UploadUserFileParam;
import com.airxiechao.y20.common.artifact.ArtifactUtil;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumArtifactFileDestDir;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.PushArtifactFileStepRunParam;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@StepRun(
        type = EnumStepRunType.PUSH_ARTIFACT_FILE_TYPE,
        name = EnumStepRunType.PUSH_ARTIFACT_FILE_NAME,
        paramClass = PushArtifactFileStepRunParam.class)
public class PushArtifactFileStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(PushArtifactFileStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private IAgentRpcClient agentRpcClient;
    private ChannelHandlerContext agentRpcClientContext;

    private PushArtifactFileStepRunParam param;
    private boolean flagStop = false;

    public PushArtifactFileStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), PushArtifactFileStepRunParam.class);
    }

    @Override
    public void assembleAgentRpcClientContext(IAgentRpcClient agentRpcClient, ChannelHandlerContext agentRpcClientContext){
        this.agentRpcClient = agentRpcClient;
        this.agentRpcClientContext = agentRpcClientContext;
    }

    @Override
    protected Response run() throws Exception {

        AgentRestClient agentRestClient = new AgentRestClient(
                agentRpcClient, agentRpcClientContext,
                agentClientConfig.getServerHost(),
                agentClientConfig.isServerRestUseSsl(),
                agentClientConfig.getAccessToken());

        String srcPath = null;
        if(Paths.get(param.getSrcPath()).isAbsolute()){
            srcPath = param.getSrcPath();
        }else{
            srcPath = Paths.get(param.getWorkingDir(), param.getSrcPath()).toString();
        }

        if(!Files.exists(Paths.get(srcPath))){
            String errorMessage = String.format("srcPath [%s] not exists", srcPath);
            appendLogLine(errorMessage);
            return new Response().error(errorMessage);
        }

        String destPath = param.getDestPath().replace("\\", "/");
        while(destPath.startsWith("/") || destPath.startsWith(".")){
            destPath = destPath.substring(1);
        }

        String uploadPipelineRunDir = ArtifactUtil.getPipelineRunFilePath(
                env.getUserId(), env.getProjectId(), env.getPipelineId(), env.getPipelineRunId(), null, null);
        String uploadPipelineDir = ArtifactUtil.getPipelineFilePath(
                env.getUserId(), env.getProjectId(), env.getPipelineId(), null, null);
        String uploadProjectDir = ArtifactUtil.getProjectFilePath(
                env.getUserId(), env.getProjectId(), null, null);

        String uploadDir;
        switch (param.getDestDir()){
            case EnumArtifactFileDestDir.PROJECT:
                uploadDir = uploadProjectDir;
                break;
            case EnumArtifactFileDestDir.PIPELINE:
                uploadDir = uploadPipelineDir;
                break;
            case EnumArtifactFileDestDir.PIPELINE_RUN:
            default:
                uploadDir = uploadPipelineRunDir;
                break;
        }

        destPath = String.format("%s%s", uploadDir, destPath);

        boolean isSrcDir = Files.isDirectory(Paths.get(srcPath));
        boolean isDestDir = destPath.endsWith("/");

        if(isSrcDir && !isDestDir){
            String errorMessage = String.format("srcPath [%s] is dir but destPath [%s] is not dir", srcPath, destPath);
            logger.error(errorMessage);
            return new Response().error(errorMessage);
        }

        // get all file map
        Map<String, String> fileMap = new HashMap<>();
        if(isSrcDir) {
            for(String srcFile : listDirDeeply(srcPath, null)){
                String src = Paths.get(srcPath, srcFile).toString();
                String dest = destPath + srcFile.replace("\\", "/");
                fileMap.put(src, dest);
            }
        }else{
            if(isDestDir){
                String srcFileName = Paths.get(srcPath).getFileName().toString();
                fileMap.put(srcPath, destPath + srcFileName);
            }else{
                fileMap.put(srcPath, destPath);
            }
        }

        // push file
        for(Map.Entry<String, String> entry : fileMap.entrySet()) {
            String src = entry.getKey();
            String dest = entry.getValue();

            String log =  String.format("push file [%s] to [%s]", src, dest);
            appendLogLine(log);

            FileData fileData = new FileData(src);
            long fileSize = fileData.getFileItem().getFileSize();
            Response uploadResp = agentRestClient.get(IAgentArtifactRest.class, (total, speed) -> {
                appendLogLine(String.format("push: %d kB, speed: %d kB/s, progress: %.2f%%",
                        total / 1024, speed / 1024, fileSize > 0 ? total * 100.0 / fileSize : 0));
            }, () -> flagStop).uploadUserFile(
                    new UploadUserFileParam(dest, fileData));
            if (!uploadResp.isSuccess()) {
                String errorMessage = log + " error";
                logger.error(errorMessage);
                appendLogLine(errorMessage);
                return new Response().error(errorMessage);
            }

            if(flagStop){
                throw new Exception("stop by flag");
            }
        }

        Map<String, String> outputData = new HashMap<>();
        if(!StringUtil.isBlank(param.getOutput())){
            outputData.put(param.getOutput(), uploadDir);
        }
        return new Response().data(outputData);
    }

    @Override
    public Response stop() throws Exception {
        this.flagStop = true;
        return new Response();
    }

    private static List<String> listDirDeeply(String path, String parentPath) {
        if(StringUtil.isBlank(parentPath)){
            parentPath = path;
        }
        Path dir = Paths.get(path);
        Path parentDir = Paths.get(parentPath);
        List<String> list = new ArrayList<>();
        try {
            Files.list(dir).forEach(subPath -> {
                if(Files.isDirectory(subPath)){
                    list.addAll(listDirDeeply(subPath.toString(), parentDir.toString()));
                }else{
                    list.add(parentDir.relativize(subPath).toString());
                }
            });
        } catch (IOException e) {
            logger.error("list dir error", path);
        }

        return list;
    }
}
