package com.airxiechao.y20.pipeline.rpc.handler.explorer;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rpc.common.RpcExchange;
import com.airxiechao.axcboot.communication.rpc.util.RpcUtil;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.y20.pipeline.rpc.api.ILocalExplorerRunRpc;
import com.airxiechao.y20.pipeline.rpc.param.explorer.*;
import com.airxiechao.y20.pipeline.run.explorer.AbstractLocalExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;
import com.airxiechao.y20.pipeline.run.explorer.LocalExplorerRunInstanceFactory;

import java.util.List;

public class LocalExplorerRunRpcHandler implements ILocalExplorerRunRpc {

    @Override
    public Response<String> createExplorerRun(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        CreateExplorerRunRpcParam param = RpcUtil.getObjectParam(rpcExchange, CreateExplorerRunRpcParam.class);

        IExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().createLocalExplorerRun(param.getWorkingDir());
        return new Response().data(explorerRun.getExplorerRunInstanceUuid());
    }

    @Override
    public Response destroyExplorerRun(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        DestroyExplorerRunRpcParam param = RpcUtil.getObjectParam(rpcExchange, DestroyExplorerRunRpcParam.class);

        LocalExplorerRunInstanceFactory.getInstance().destroyLocalExplorerRun(param.getExplorerRunInstanceUuid());
        return new Response();
    }

    @Override
    public Response<FsFile> getExplorerRunFile(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        GetExplorerRunFileRpcParam param = RpcUtil.getObjectParam(rpcExchange, GetExplorerRunFileRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        FsFile file = explorerRun.getFile(param.getPath());
        return new Response().data(file);
    }

    @Override
    public Response<List<FsFile>> listExplorerRun(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        ListExplorerRunRpcParam param = RpcUtil.getObjectParam(rpcExchange, ListExplorerRunRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        List<FsFile> list = explorerRun.list(param.getPath());
        return new Response().data(list);
    }

    @Override
    public Response<byte[]> pullExplorerRunFilePart(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        PullExplorerRunFilePartRpcParam param = RpcUtil.getObjectParam(rpcExchange, PullExplorerRunFilePartRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        byte[] bytes = explorerRun.pullFilePart(param.getPath(), param.getPos(), param.getLength());
        return new Response().data(bytes);
    }

    @Override
    public Response pushExplorerRunFilePart(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;

        PushExplorerRunFilePartRpcParam param = RpcUtil.getObjectParam(rpcExchange, PushExplorerRunFilePartRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        boolean pushed = explorerRun.pushFilePart(param.getFileHolderId(), param.getPos(), param.getBytes());
        if(pushed){
            return new Response();
        }else{
            return new Response().error();
        }
    }

    @Override
    public Response<String> createExplorerRunFileHolder(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        CreateExplorerRunFileHolderRpcParam param = RpcUtil.getObjectParam(rpcExchange, CreateExplorerRunFileHolderRpcParam.class);

        AbstractLocalExplorerRunInstance explorer = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        String holderId = explorer.createFileHolder(param.getPath(), param.getSize());
        return new Response().data(holderId);
    }

    @Override
    public Response<String> saveExplorerRunFileHolder(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        SaveExplorerRunFileHolderRpcParam param = RpcUtil.getObjectParam(rpcExchange, SaveExplorerRunFileHolderRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        String path = explorerRun.saveFileHolder(param.getFileHolderId());
        return new Response().data(path);
    }

    @Override
    public Response removeExplorerRunFileHolder(Object exc) throws Exception {
        RpcExchange rpcExchange = (RpcExchange)exc;
        RemoveExplorerRunFileHolderRpcParam param = RpcUtil.getObjectParam(rpcExchange, RemoveExplorerRunFileHolderRpcParam.class);

        AbstractLocalExplorerRunInstance explorerRun = LocalExplorerRunInstanceFactory.getInstance().getLocalExplorerRun(param.getExplorerRunInstanceUuid());
        explorerRun.removeFileHolder(param.getFileHolderId());
        return new Response();
    }
}
