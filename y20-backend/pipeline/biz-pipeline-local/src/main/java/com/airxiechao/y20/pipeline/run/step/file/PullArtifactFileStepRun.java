package com.airxiechao.y20.pipeline.run.step.file;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.artifact.rest.api.IAgentArtifactRest;
import com.airxiechao.y20.artifact.rest.param.DownloadUserFileParam;
import com.airxiechao.y20.artifact.rest.param.ListUserFileParam;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.pojo.constant.EnumStepRunType;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import com.airxiechao.y20.pipeline.run.step.param.PullArtifactFileStepRunParam;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@StepRun(
        type = EnumStepRunType.PULL_ARTIFACT_FILE_TYPE,
        name = EnumStepRunType.PULL_ARTIFACT_FILE_NAME,
        paramClass = PullArtifactFileStepRunParam.class)
public class PullArtifactFileStepRun extends AbstractStepRunInstance {

    private static final Logger logger = LoggerFactory.getLogger(PullArtifactFileStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    private IAgentRpcClient agentRpcClient;
    private ChannelHandlerContext agentRpcClientContext;

    private PullArtifactFileStepRunParam param;
    private boolean flagStop = false;

    public PullArtifactFileStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assemble(PipelineStep step) {
        param = ModelUtil.fromMapAndCheckRequiredField(step.getParams(), PullArtifactFileStepRunParam.class);
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
                agentClientConfig.getServerRestPort(),
                agentClientConfig.isServerRestUseSsl(),
                agentClientConfig.getAccessToken());

        String destPath = null;
        if(Paths.get(param.getDestPath()).isAbsolute()){
            destPath = param.getDestPath();
        }else{
            destPath = Paths.get(param.getWorkingDir(), param.getDestPath()).toFile().getCanonicalPath();
        }

        String srcPath = param.getSrcPath();

        boolean isSrcDir = srcPath.endsWith("/");
        boolean isDestDir = Files.isDirectory(Paths.get(destPath)) || param.getDestPath().endsWith("/") || param.getDestPath().endsWith("\\");

        if(isSrcDir && !isDestDir){
            String errorMessage = String.format("srcPath [%s] is dir but destPath [%s] is not dir", srcPath, destPath);
            logger.error(errorMessage);
            return new Response().error(errorMessage);
        }

        if(isDestDir){
            FileUtil.mkDirs(destPath);
        }else{
            FileUtil.mkDirs(Paths.get(destPath).getParent().toString());
        }

        // get all file map
        Map<FsFile, String> fileMap = new HashMap<>();

        // list src
        List<FsFile> srcList = listDeeply(agentRestClient, srcPath);

        if(isSrcDir){
            for (FsFile srcFile : srcList) {
                fileMap.put(srcFile, Paths.get(destPath, srcFile.getName()).toString());
            }
        }else{
            if(srcList.size() > 0){
                FsFile srcFile = srcList.get(0);
                if(isDestDir){
                    String srcFileName = Paths.get(srcPath).getFileName().toString();
                    fileMap.put(srcFile, Paths.get(destPath, srcFileName).toString());
                }else{
                    fileMap.put(srcFile, destPath);
                }
            }
        }

        // pull file
        for(Map.Entry<FsFile, String> entry : fileMap.entrySet()){
            FsFile src = entry.getKey();
            String dest = entry.getValue();

            String log = String.format("pull file [%s] to [%s]", src.getPath(), dest);
            appendLogLine(log);

            try(OutputStream outputStream = new FileOutputStream(dest)){
                agentRestClient.get(IAgentArtifactRest.class, outputStream, (total, speed) -> {
                    appendLogLine(String.format("read: %d kB, speed: %d kB/s, progress: %.2f%%",
                            total / 1024, speed / 1024, src.getSize() > 0 ? total * 100.0 / src.getSize() : 0));
                }, () -> flagStop).downloadUserFile(
                        new DownloadUserFileParam(src.getPath()));
            }catch (Exception e){
                String errorMessage = log + " error";
                logger.error(errorMessage, e);
                appendLogLine(errorMessage);
                return new Response().error(errorMessage);
            }

            if(flagStop){
                throw new Exception("stop by flag");
            }
        }

        return new Response();
    }

    @Override
    public Response stop() throws Exception {
        this.flagStop = true;
        return new Response();
    }

    private List<FsFile> listDeeply(AgentRestClient agentRestClient, String path) throws Exception {

        List<FsFile> list = new ArrayList<>();

        Response<List<FsFile>> srcListResp = agentRestClient.get(IAgentArtifactRest.class).listUserFile(
                new ListUserFileParam(path));
        if(!srcListResp.isSuccess()){
            logger.error("list src {} error: {}", path, srcListResp.getMessage());
            throw new Exception(srcListResp.getMessage());
        }

        List<FsFile> srcList = srcListResp.getData();
        for (FsFile srcFile : srcList) {
            if(srcFile.isDir()){
                list.addAll(listDeeply(agentRestClient, srcFile.getPath()));
            }else
                list.add(srcFile);
        }

        return list;
    }
}
