package com.airxiechao.y20.pipeline.run.step.env;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.FileUtil;
import com.airxiechao.y20.agent.pojo.config.AgentClientConfig;
import com.airxiechao.y20.agent.rpc.api.client.IAgentRpcClient;
import com.airxiechao.y20.artifact.rest.api.IAgentArtifactRest;
import com.airxiechao.y20.artifact.rest.param.DownloadUserFileParam;
import com.airxiechao.y20.artifact.rest.param.ListUserFileParam;
import com.airxiechao.y20.common.core.rest.AgentRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.run.step.AbstractStepRunInstance;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractPrepareEnvStepRun extends AbstractStepRunInstance {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractPrepareEnvStepRun.class);
    private static final AgentClientConfig agentClientConfig = ConfigFactory.get(AgentClientConfig.class);

    protected IAgentRpcClient agentRpcClient;
    protected ChannelHandlerContext agentRpcClientContext;
    protected boolean flagStop = false;

    public AbstractPrepareEnvStepRun(String stepRunInstanceUuid, Env env) {
        super(stepRunInstanceUuid, env);
    }

    @Override
    public void assembleAgentRpcClientContext(IAgentRpcClient agentRpcClient, ChannelHandlerContext agentRpcClientContext){
        this.agentRpcClient = agentRpcClient;
        this.agentRpcClientContext = agentRpcClientContext;
    }

    @Override
    public Response stop() throws Exception {
        this.flagStop = true;
        return new Response();
    }

    protected void linkNativeCacheDirs(String dirs, String cacheDir, String workingDir){
        List<String> dirList = Arrays.stream(dirs.split(",")).map(dir -> dir.strip()).collect(Collectors.toList());

        // link cache dirs
        dirList.forEach(dir -> {

            appendLogLine(String.format("link cache dir: %s", dir));

            // target dir
            Path cacheTargetPath = Paths.get(cacheDir, dir);
            FileUtil.mkDirs(cacheTargetPath.toString());

            // link dir
            Path cacheLinkPath = Paths.get(workingDir, dir);
            FileUtil.mkDirs(cacheLinkPath.getParent().toString());
            try {
                Files.createSymbolicLink(cacheLinkPath, cacheTargetPath);
            } catch (Exception e) {
                String errMessage = String.format("link cache dir [%s] error: %s", dir, e.getMessage());
                appendLogLine(errMessage);
                logger.error(errMessage, e);
            }
        });

    }

    protected Map<String, String> linkDockerCacheDirs(String dirs, String cacheDir, String workingDir){
        Map<String, String> binds = new HashMap<>();

        List<String> dirList = Arrays.stream(dirs.split(",")).map(dir -> dir.strip()).collect(Collectors.toList());

        // link cache dirs
        dirList.forEach(dir -> {

            appendLogLine(String.format("link cache dir: %s", dir));

            // target dir
            String cacheTargetDir = Paths.get(cacheDir, dir).toString();
            FileUtil.mkDirs(cacheTargetDir);

            // link dir
            Path cacheLinkPath = Paths.get(workingDir, dir);
            FileUtil.mkDirs(cacheLinkPath.getParent().toString());
            try {
                binds.put(cacheTargetDir, cacheLinkPath.toFile().getCanonicalFile().toString());
            } catch (IOException e) {
                logger.error("link docker cache dir {} error", dir, e);
            }

        });

        return binds;

    }

    protected void syncArtifactFile(String srcDir, String destDir){

        AgentRestClient agentRestClient = new AgentRestClient(
                agentRpcClient, agentRpcClientContext,
                agentClientConfig.getServerHost(),
                agentClientConfig.getServerRestPort(),
                agentClientConfig.isServerRestUseSsl(),
                agentClientConfig.getAccessToken());

        List<FsFile> list = listDeeply(agentRestClient, srcDir);
        for(FsFile f : list){
            if(flagStop){
                break;
            }

            String srcPath = f.getPath();
            String middleDir = srcPath.substring(srcDir.length(), srcPath.lastIndexOf(f.getName()));
            String destPath = Paths.get(destDir, middleDir, f.getName()).toString();

            appendLogLine("sync artifact file: " + srcPath);

            FileUtil.mkDirs(Paths.get(destDir, middleDir).toString());

            try(OutputStream outputStream = new FileOutputStream(destPath)){
                agentRestClient.get(IAgentArtifactRest.class, outputStream, (total, speed) -> {
                    appendLogLine(String.format("read: %d kB, speed: %d kB/s, progress: %.2f%%",
                            total / 1024, speed / 1024, f.getSize() > 0 ? total * 100.0 / f.getSize() : 0));
                }, () -> flagStop).downloadUserFile(
                        new DownloadUserFileParam(srcPath));
            }catch (Exception e){
                String errorMessage = String.format("pull artifact file [%s] error", f.getPath());
                appendLogLine(errorMessage);
                logger.error(errorMessage, e);
            }
        }
    }

    private List<FsFile> listDeeply(AgentRestClient agentRestClient, String path) {

        List<FsFile> list = new ArrayList<>();

        Response<List<FsFile>> srcListResp = agentRestClient.get(IAgentArtifactRest.class).listUserFile(
                new ListUserFileParam(path));
        if(!srcListResp.isSuccess()){
            logger.error("list src {} error: {}", path, srcListResp.getMessage());
            return list;
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