package com.airxiechao.y20.pipeline.run.explorer;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.axcboot.util.function.TriConsumer;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.common.core.rest.ServiceCallAgentClient;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.rpc.api.ILocalExplorerRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.explorer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RemoteExplorerRunInstance implements IExplorerRunInstance{

    private static final Logger logger = LoggerFactory.getLogger(RemoteExplorerRunInstance.class);

    protected String explorerRunInstanceUuid;
    private String pipelineRunInstanceUuid;
    private Env env;

    private Map<String, Boolean> flagStopDownloadMap = new ConcurrentHashMap<>();
    private Map<String, Boolean> flagStopUploadMap = new ConcurrentHashMap<>();

    public RemoteExplorerRunInstance(String pipelineRunInstanceUuid, Env env){
        this.pipelineRunInstanceUuid = pipelineRunInstanceUuid;
        this.env = env;
    }

    @Override
    public void create() throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();

        String workingDir = !StringUtil.isBlank(env.getDockerHostWorkingDir()) ? env.getDockerHostWorkingDir() : env.getWorkingDir();
        Response resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).createExplorerRun(
                new CreateExplorerRunRpcParam(pipelineRunInstanceUuid, env.getDockerContainerId(), workingDir));
        if(resp.isSuccess()){
            this.explorerRunInstanceUuid = (String)resp.getData();
        }
    }

    @Override
    public void destroy() throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        // stop all upload and download
        flagStopUploadMap.keySet().forEach(path -> {
            stopUpload(path);
        });
        flagStopDownloadMap.keySet().forEach(path -> {
            stopDownload(path);
        });

        String clientId = agentResp.getData().getClientId();
        ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).destroyExplorerRun(
                new DestroyExplorerRunRpcParam(explorerRunInstanceUuid));
    }

    @Override
    public FsFile getFile(String path) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response<FsFile> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).getExplorerRunFile(
                new GetExplorerRunFileRpcParam(explorerRunInstanceUuid, path));
        if(!resp.isSuccess()){
            throw new Exception("get explorer file error");
        }

        return resp.getData();
    }

    @Override
    public List<FsFile> list(String path) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response<List<FsFile>> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).listExplorerRun(
                new ListExplorerRunRpcParam(explorerRunInstanceUuid, path));
        if(!resp.isSuccess()){
            throw new Exception("list explorer error");
        }

        return resp.getData();
    }

    @Override
    public void download(String path, OutputStream outputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();

        // get size
        Response<FsFile> getFileResp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).getExplorerRunFile(
                new GetExplorerRunFileRpcParam(explorerRunInstanceUuid, path));
        if(!getFileResp.isSuccess()){
            throw new Exception("get explorer file error");
        }

        FsFile file = getFileResp.getData();
        long size = file.getSize();

        flagStopDownloadMap.put(path, false);

        // download
        long pos = 0;
        final int length = 1024 * 500;
        if(size > 0){
            while(true) {
                if(flagStopDownloadMap.get(path)){
                    throw new Exception("stop by flag");
                }

                long time0 = System.currentTimeMillis();

                Response<byte[]> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).pullExplorerRunFilePart(
                                new PullExplorerRunFilePartRpcParam(explorerRunInstanceUuid, path, pos, length));
                if(!resp.isSuccess()){
                    throw new Exception("pull file part error");
                }

                byte[] bytes = resp.getData();
                if(null != bytes){
                    outputStream.write(bytes);
                    outputStream.flush();

                    pos += bytes.length;
                }

                // report total/speed/progress
                long time1 = System.currentTimeMillis();
                double secs = (time1 - time0) / 1000.0;
                long speed = (long)(bytes.length / secs);
                double progress = (double)pos / size;

                totalSpeedProgressConsumer.accept(size, speed, progress);

                if(null == bytes || bytes.length < length){
                    break;
                }
            }
        }
    }

    @Override
    public void stopDownload(String path) {
        flagStopDownloadMap.put(path, true);
    }

    @Override
    public void upload(String path, long size, Long maxSpeed, InputStream inputStream,
                       TriConsumer<Long, Long, Double> totalSpeedProgressConsumer
    ) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        flagStopUploadMap.put(path, false);

        String clientId = agentResp.getData().getClientId();
        Response<String> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).createExplorerRunFileHolder(
                        new CreateExplorerRunFileHolderRpcParam(explorerRunInstanceUuid, path, size));
        if(!resp.isSuccess()){
            throw new Exception("create file holder error");
        }

        String fileHolderId = resp.getData();

        long pos = 0;
        int length = 1024 * 500;
        if(size > 0){
            while(true){
                if(flagStopUploadMap.get(path)){
                    throw new Exception("stop by flag");
                }

                long time0 = System.currentTimeMillis();

                byte[] bytes = inputStream.readNBytes(length);
                if(bytes.length > 0){

                    resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).pushExplorerRunFilePart(
                            new PushExplorerRunFilePartRpcParam(explorerRunInstanceUuid, fileHolderId, pos, bytes));
                    if(!resp.isSuccess()){
                        throw new Exception("push file part error");
                    }
                }

                pos += bytes.length;

                long time1;
                long speed;
                while (true){
                    time1 = System.currentTimeMillis();
                    double secs = (time1 - time0) / 1000.0;
                    speed = (long)(bytes.length / secs);

                    if(null == maxSpeed || speed <= maxSpeed){
                        break;
                    }else{
                        Thread.sleep((long)((double)bytes.length / maxSpeed - secs) * 1000);
                    }
                }

                double progress = (double)pos / size;

                // report total/speed/progress
                totalSpeedProgressConsumer.accept(size, speed, progress);

                if(bytes.length < length){
                    break;
                }
            }
        }

        if(pos != size){
            throw new Exception("push file part error");
        }

        resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).saveExplorerRunFileHolder(
                        new SaveExplorerRunFileHolderRpcParam(explorerRunInstanceUuid, fileHolderId));
        if(!resp.isSuccess()){
            throw new Exception("save file holder error");
        }
    }

    @Override
    public void stopUpload(String path) {
        flagStopUploadMap.put(path, true);
    }

    @Override
    public String createFileHolder(String path, long size) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response<String> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).createExplorerRunFileHolder(
                new CreateExplorerRunFileHolderRpcParam(explorerRunInstanceUuid, path, size));
        if(!resp.isSuccess()){
            throw new Exception("create file holder error");
        }

        String fileHolderId = resp.getData();
        return fileHolderId;
    }

    @Override
    public boolean pushFilePart(String holderId, long pos, byte[] bytes) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).pushExplorerRunFilePart(
                new PushExplorerRunFilePartRpcParam(explorerRunInstanceUuid, holderId, pos, bytes));
        return resp.isSuccess();
    }

    @Override
    public String saveFileHolder(String holderId) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response<String> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).saveExplorerRunFileHolder(
                new SaveExplorerRunFileHolderRpcParam(explorerRunInstanceUuid, holderId));
        if(!resp.isSuccess()){
            throw new Exception("save file holder error");
        }

        String path = resp.getData();
        return path;
    }

    @Override
    public boolean removeFileHolder(String holderId) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).removeExplorerRunFileHolder(
                new RemoveExplorerRunFileHolderRpcParam(explorerRunInstanceUuid, holderId));
        return resp.isSuccess();
    }

    @Override
    public byte[] pullFilePart(String path, long pos, int length) throws Exception {
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(env.getUserId(), env.getAgentId()));

        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        String clientId = agentResp.getData().getClientId();
        Response<byte[]> resp = ServiceCallAgentClient.get(ILocalExplorerRunRpc.class, clientId).pullExplorerRunFilePart(
                new PullExplorerRunFilePartRpcParam(explorerRunInstanceUuid, path, pos, length));
        if(!resp.isSuccess()){
            throw new Exception("pull file part error");
        }

        byte[] bytes = resp.getData();
        return bytes;
    }

    @Override
    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }
}
