package com.airxiechao.y20.pipeline.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.function.TriConsumer;
import com.airxiechao.y20.agent.pojo.vo.AgentVo;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.GetAgentParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.biz.api.IExplorerRunInstanceBiz;
import com.airxiechao.y20.pipeline.biz.api.IPipelineRunInstanceBiz;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.pipeline.IPipelineRunInstance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExplorerRunInstanceBizProcess implements IExplorerRunInstanceBiz {

    private IPipelineRunInstanceBiz pipelineRunInstanceBiz = Biz.get(IPipelineRunInstanceBiz.class);

    @Override
    public IExplorerRunInstance createExplorerRunInstance(String pipelineRunInstanceUuid) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        Env currentEnv = pipelineRunInstance.getCurrentEnv();
        if(null == currentEnv){
            throw new Exception("no env");
        }

        Long userId = currentEnv.getUserId();
        String agentId = currentEnv.getAgentId();
        Response<AgentVo> agentResp = ServiceRestClient.get(IServiceAgentRest.class).getAgent(
                new GetAgentParam(userId, agentId));
        if(!agentResp.isSuccess()){
            throw new Exception("no agent");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.createExplorerRunInstance();
        return explorerRunInstance;
    }

    @Override
    public void destroyExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        explorerRunInstance.destroy();
    }

    @Override
    public List<FsFile> listFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.list(path);
    }

    @Override
    public void downloadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, OutputStream outputStream,
                                                TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        explorerRunInstance.download(path, outputStream, totalSpeedProgressConsumer);
    }

    @Override
    public void stopDownloadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        explorerRunInstance.stopDownload(path);
    }

    @Override
    public void uploadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long size, Long maxSpeed,
                                              InputStream inputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        explorerRunInstance.upload(path, size, maxSpeed, inputStream, totalSpeedProgressConsumer);
    }

    @Override
    public void stopUploadFileExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        explorerRunInstance.stopUpload(path);
    }

    @Override
    public String createFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long size) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.createFileHolder(path, size);
    }

    @Override
    public boolean pushFilePartExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId, long pos, byte[] bytes) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.pushFilePart(holderId, pos, bytes);
    }

    @Override
    public String saveFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.saveFileHolder(holderId);
    }

    @Override
    public boolean removeFileHolderExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String holderId) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.removeFileHolder(holderId);
    }

    @Override
    public byte[] pullFilePartExplorerRunInstance(String pipelineRunInstanceUuid, String explorerRunInstanceUuid, String path, long pos, int length) throws Exception {
        IPipelineRunInstance pipelineRunInstance = pipelineRunInstanceBiz.getRunInstance(pipelineRunInstanceUuid);
        if(null == pipelineRunInstance){
            throw new Exception("no pipeline run instance");
        }

        IExplorerRunInstance explorerRunInstance = pipelineRunInstance.getExplorerRunInstance(explorerRunInstanceUuid);
        if(null == explorerRunInstance){
            throw new Exception("no explorer run instance");
        }

        return explorerRunInstance.pullFilePart(path, pos, length);
    }
}
