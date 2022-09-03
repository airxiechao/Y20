package com.airxiechao.y20.pipeline.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.pubsub.EventBus;
import com.airxiechao.y20.pipeline.biz.api.IExplorerRunInstanceBiz;
import com.airxiechao.y20.pipeline.pojo.config.PipelineRunInstanceConfig;
import com.airxiechao.y20.pipeline.pojo.constant.EnumExplorerRunTransferDirection;
import com.airxiechao.y20.pipeline.pubsub.event.explorer.EventExplorerRunTransferProgress;
import com.airxiechao.y20.pipeline.pubsub.event.terminal.EventTerminalRunOutputPush;
import com.airxiechao.y20.pipeline.rest.api.IServiceExplorerRunInstanceRest;
import com.airxiechao.y20.pipeline.rest.param.*;
import com.airxiechao.y20.pipeline.run.explorer.IExplorerRunInstance;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

public class ServiceExplorerRunInstanceRestHandler implements IServiceExplorerRunInstanceRest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceExplorerRunInstanceRestHandler.class);
    private static final PipelineRunInstanceConfig config = ConfigFactory.get(PipelineRunInstanceConfig.class);
    private IExplorerRunInstanceBiz explorerRunInstanceBiz = Biz.get(IExplorerRunInstanceBiz.class);

    @Override
    public Response<String> createExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateTerminalRunInstanceParam param = RestUtil.rawJsonData(exchange, CreateTerminalRunInstanceParam.class);

        try {
            IExplorerRunInstance terminalRunInstance = explorerRunInstanceBiz.createExplorerRunInstance(param.getPipelineRunInstanceUuid());
            return new Response().data(terminalRunInstance.getExplorerRunInstanceUuid());
        } catch (Exception e) {
            logger.error("create explorer run instance error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response destroyExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DestroyExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, DestroyExplorerRunInstanceParam.class);

        try {
            explorerRunInstanceBiz.destroyExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(), param.getExplorerRunInstanceUuid());
            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response<List<FsFile>> listFileExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ListFileExplorerRunInstanceParam param = RestUtil.queryData(exchange, ListFileExplorerRunInstanceParam.class);

        try {
            List<FsFile> list = explorerRunInstanceBiz.listFileExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath());
            return new Response().data(list);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public void downloadFileExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        DownloadFileExplorerRunInstanceParam param = RestUtil.queryData(exchange, DownloadFileExplorerRunInstanceParam.class);

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, Paths.get(param.getPath()).getFileName().toString());

        try {
            explorerRunInstanceBiz.downloadFileExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath(),
                    exchange.getOutputStream(),
                    (total, speed, progress) -> {

                    });
        } catch (Exception e) {
            logger.error("download file explorer run instance error", e);
            return;
        }
    }

    @Override
    public Response stopDownloadFileExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopDownloadFileExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, StopDownloadFileExplorerRunInstanceParam.class);

        try {
            explorerRunInstanceBiz.stopDownloadFileExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath());
            return new Response();
        } catch (Exception e) {
            logger.error("stop download file explorer run instance error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response uploadFileExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        UploadFileExplorerRunInstanceParam param = RestUtil.multiPartFormData(exchange, UploadFileExplorerRunInstanceParam.class);

        try (InputStream inputStream = param.getFile().getFileItem().getInputStream()){
            explorerRunInstanceBiz.uploadFileExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath(),
                    param.getSize(),
                    config.getExplorerRunUploadMaxSpeed(),
                    inputStream,
                    (total, speed, progress) -> {
                        EventBus.getInstance().publish(
                                new EventExplorerRunTransferProgress(
                                        param.getPipelineRunInstanceUuid(),
                                        param.getExplorerRunInstanceUuid(),
                                        param.getPath(),
                                        EnumExplorerRunTransferDirection.UPLOAD,
                                        total,
                                        speed,
                                        progress));

                    });
            return new Response();
        } catch (Exception e) {
            logger.error("upload file explorer run instance error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response stopUploadFileExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        StopUploadFileExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, StopUploadFileExplorerRunInstanceParam.class);

        try {
            explorerRunInstanceBiz.stopUploadFileExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath());
            return new Response();
        } catch (Exception e) {
            logger.error("stop upload file explorer run instance error", e);
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response<String> createFileHolderExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        CreateFileHolderExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, CreateFileHolderExplorerRunInstanceParam.class);

        try {
            String path = explorerRunInstanceBiz.createFileHolderExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath(),
                    param.getSize());
            return new Response().data(path);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response pushFilePartExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        PushFilePartExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, PushFilePartExplorerRunInstanceParam.class);

        try {
            boolean pushed = explorerRunInstanceBiz.pushFilePartExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getHolderId(),
                    param.getPos(),
                    param.getBytes());
            if(!pushed){
                return new Response().error();
            }else{
                return new Response();
            }
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response<String> saveFileHolderExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        SaveFileHolderExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, SaveFileHolderExplorerRunInstanceParam.class);

        try {
            String path = explorerRunInstanceBiz.saveFileHolderExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getHolderId());
            return new Response().data(path);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response removeFileHolderExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        RemoveFileHolderExplorerRunInstanceParam param = RestUtil.rawJsonData(exchange, RemoveFileHolderExplorerRunInstanceParam.class);

        try {
            boolean removed = explorerRunInstanceBiz.removeFileHolderExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getHolderId());
            if(!removed){
                return new Response().error();
            }else {
                return new Response();
            }
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response<byte[]> pullFilePartExplorerRunInstance(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        PullFilePartExplorerRunInstanceParam param = RestUtil.queryData(exchange, PullFilePartExplorerRunInstanceParam.class);

        try {
            byte[] bytes = explorerRunInstanceBiz.pullFilePartExplorerRunInstance(
                    param.getPipelineRunInstanceUuid(),
                    param.getExplorerRunInstanceUuid(),
                    param.getPath(),
                    param.getPos(),
                    param.getLength());
            return new Response().data(bytes);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }
}
