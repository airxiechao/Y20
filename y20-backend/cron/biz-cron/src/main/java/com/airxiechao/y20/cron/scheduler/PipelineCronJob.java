package com.airxiechao.y20.cron.scheduler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.storage.cache.redis.RedisLock;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.cron.lock.PipelineCronTaskLockFactory;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceCreatePipelineRunParam;
import com.airxiechao.y20.pipeline.rest.param.ServiceStartPipelineRunParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PipelineCronJob implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(PipelineCronJob.class);

    private Long pipelineId;
    private Integer intervalSecs;
    private Map<String, String> inParams;

    public PipelineCronJob(Long pipelineId, Integer intervalSecs, Map<String, String> inParams) {
        this.pipelineId = pipelineId;
        this.intervalSecs = intervalSecs;
        this.inParams = inParams;
    }

    @Override
    public void run() {
        // try to get lock
        try( RedisLock lock = PipelineCronTaskLockFactory.getInstance().get(pipelineId, Math.max(1, intervalSecs - 1))){
            if(!lock.tryLock()){
                return;
            }

            logger.info("pipeline [{}] cron job run", pipelineId);

            Response<Long> createResp =  ServiceRestClient.get(IServicePipelineRest.class).createPipelineRun(
                    new ServiceCreatePipelineRunParam(
                            pipelineId,
                            "定时 " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),
                            inParams,
                            false
                    ));

            if(!createResp.isSuccess()){
                logger.error("create pipeline [{}] run error: {}", pipelineId, createResp.getMessage());
                return;
            }

            Long pipelineRunId = createResp.getData();
            Response<Long> startResp =  ServiceRestClient.get(IServicePipelineRest.class).startPipelineRun(
                    new ServiceStartPipelineRunParam(pipelineRunId));

            if(!startResp.isSuccess()){
                logger.error("start pipeline [{}] run [{}] error: {}", pipelineId, pipelineRunId, startResp.getMessage());
                return;
            }

            logger.info("start pipeline [{}] run [{}] complete", pipelineId, pipelineRunId);
        }catch (Exception e){
            logger.error("pipeline [{}] cron task error", pipelineId, e);
        }
    }
}
