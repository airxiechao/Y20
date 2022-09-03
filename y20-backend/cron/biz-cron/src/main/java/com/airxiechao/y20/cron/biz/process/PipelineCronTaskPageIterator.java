package com.airxiechao.y20.cron.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.cron.pojo.PipelineCronTask;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicPageVo;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineBasicVo;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.ServiceListScheduledPipelineParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineCronTaskPageIterator implements Iterator<List<PipelineCronTask>> {

    private static Logger logger = LoggerFactory.getLogger(PipelineCronTaskPageIterator.class);

    private long fromPipelineId = 1L;
    private long toPipelineId;
    private long pageSize;
    private boolean hasMore = true;

    public PipelineCronTaskPageIterator(long pageSize){
        this.pageSize = pageSize;
        toPipelineId = fromPipelineId + pageSize;
    }

    @Override
    public boolean hasNext() {
        return hasMore;
    }

    @Override
    public List<PipelineCronTask> next() {
        PipelineBasicPageVo pageVo = listScheduledPipelinePage(fromPipelineId, toPipelineId);
        if(null == pageVo){
            hasMore = false;
            return new ArrayList<>();
        }

        if(pageVo.isHasMore()){
            hasMore = true;
            fromPipelineId = toPipelineId;
            toPipelineId = fromPipelineId + pageSize;
        }else{
            hasMore = false;
        }

        List<PipelineBasicVo> list = pageVo.getPage();
        if(null == list){
            return new ArrayList<>();
        }

        return list.stream()
                .map(pipeline -> new PipelineCronTask(pipeline.getPipelineId(), pipeline.getCronBeginTime(), pipeline.getCronIntervalSecs(), pipeline.getCronInParams()))
                .collect(Collectors.toList());
    }

    private PipelineBasicPageVo listScheduledPipelinePage(long fromPipelineId, long toPipelineId) {
        logger.info("list pipeline cron task [from:{}, to:{}]", fromPipelineId, toPipelineId);

        Response<PipelineBasicPageVo> resp = ServiceRestClient.get(IServicePipelineRest.class).listScheduledPipeline(
                new ServiceListScheduledPipelineParam(fromPipelineId, toPipelineId));

        if(!resp.isSuccess()){
            logger.error("list pipeline cron task error: {}", resp.getMessage());
            return null;
        }

        PipelineBasicPageVo pageVo = resp.getData();
        return pageVo;
    }
}
