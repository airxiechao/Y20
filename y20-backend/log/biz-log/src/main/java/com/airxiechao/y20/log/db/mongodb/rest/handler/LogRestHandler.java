package com.airxiechao.y20.log.db.mongodb.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.log.biz.api.ILogBiz;
import com.airxiechao.y20.log.rest.api.ILogRest;
import com.airxiechao.y20.log.rest.param.DownloadLogParam;
import com.airxiechao.y20.log.rest.param.GetLogParam;
import com.airxiechao.y20.pipeline.db.record.PipelineStepRunRecord;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.GetPipelineRunParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class LogRestHandler implements ILogRest {

    private static final Logger logger = LoggerFactory.getLogger(LogRestHandler.class);
    private ILogBiz logBiz = Biz.get(ILogBiz.class);

    @Override
    public Response getLog(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;
        GetLogParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetLogParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Response<PipelineRunDetailVo> runResp = ServiceRestClient.get(IServicePipelineRest.class).getPipelineRun(
                new GetPipelineRunParam(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId()));

        if(!runResp.isSuccess()){
            throw new RuntimeException(String.format("get pipeline run error: %s", runResp.getMessage()));
        }

        PipelineRunDetailVo runDetail = runResp.getData();
        if(null == runDetail){
            throw new RuntimeException("no pipeline run");
        }

        PipelineStepRunRecord stepRunRecord = runDetail.getPipelineStepRuns().get(param.getPosition());

        String log = logBiz.getLog(runDetail.getPipelineRun().getInstanceUuid(), stepRunRecord.getInstanceUuid(), 200);
        return new Response().data(log);
    }

    @Override
    public void downloadLog(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;
        DownloadLogParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, DownloadLogParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return;
        }

        Response<PipelineRunDetailVo> runResp = ServiceRestClient.get(IServicePipelineRest.class).getPipelineRun(
                new GetPipelineRunParam(param.getUserId(), param.getProjectId(), param.getPipelineId(), param.getPipelineRunId()));

        if(!runResp.isSuccess()){
            throw new RuntimeException(String.format("get pipeline run error: %s", runResp.getMessage()));
        }

        PipelineRunDetailVo runDetail = runResp.getData();
        if(null == runDetail){
            throw new RuntimeException("no pipeline run");
        }

        PipelineStepRunRecord stepRunRecord = runDetail.getPipelineStepRuns().get(param.getPosition());

        String log = logBiz.getLog(runDetail.getPipelineRun().getInstanceUuid(), stepRunRecord.getInstanceUuid(), 5000);

        exchange.startBlocking();
        RestUtil.setDownloadHerder(exchange, String.format("log-%d-%d.txt", stepRunRecord.getPipelineRunId(), stepRunRecord.getId()));

        try{
            OutputStream outputStream = exchange.getOutputStream();
            outputStream.write(log.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            logger.error("download log file error", e);
            return;
        }
    }
}
