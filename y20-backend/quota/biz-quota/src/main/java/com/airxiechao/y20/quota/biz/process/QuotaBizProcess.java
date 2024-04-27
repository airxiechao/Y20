package com.airxiechao.y20.quota.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.db.sql.model.OrderType;
import com.airxiechao.y20.agent.rest.api.IServiceAgentRest;
import com.airxiechao.y20.agent.rest.param.CountAgentParam;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pipeline.rest.api.IServicePipelineRest;
import com.airxiechao.y20.pipeline.rest.param.CountPipelineRunParam;
import com.airxiechao.y20.quota.biz.api.IQuotaBiz;
import com.airxiechao.y20.quota.db.api.IQuotaDb;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;
import com.airxiechao.y20.quota.pojo.Quota;
import com.airxiechao.y20.quota.pojo.Usage;
import com.airxiechao.y20.quota.pojo.config.QuotaConfig;
import com.airxiechao.y20.quota.pojo.vo.FreeQuotaVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QuotaBizProcess implements IQuotaBiz {

    private static final Logger logger = LoggerFactory.getLogger(QuotaBizProcess.class);
    private static final QuotaConfig quotaConfig = ConfigFactory.get(QuotaConfig.class);

    private IQuotaDb quotaDb = Db.get(IQuotaDb.class);

    @Override
    public Usage measureUsage(Long userId, Date beginTime, Date endTime) throws Exception {

        // count agent
        Response<Long> countAgentResp = ServiceRestClient.get(IServiceAgentRest.class).countAgent(new CountAgentParam(userId));
        if(!countAgentResp.isSuccess()){
            String error = String.format("count agent error: %s", countAgentResp.getMessage());
            logger.error(error);
            throw new Exception(error);
        }

        int numAgent = countAgentResp.getData().intValue();

        // count pipeline run
        Response<Long> countPipelineRunResp = ServiceRestClient.get(IServicePipelineRest.class).countPipelineRun(
                new CountPipelineRunParam(userId, beginTime, endTime));
        if(!countPipelineRunResp.isSuccess()){
            String error = String.format("count pipeline run error: %s", countPipelineRunResp.getMessage());
            logger.error(error);
            throw new Exception(error);
        }

        int numPipelineRun = countPipelineRunResp.getData().intValue();

        return new Usage(numAgent, numPipelineRun);
    }

    @Override
    public FreeQuotaVo getFreeQuota() {
        return new FreeQuotaVo(quotaConfig.getFreeQuotaNumAgent(), quotaConfig.getFreeQuotaNumPipelineRun());
    }

    @Override
    public Quota getCurrentQuota(Long userId) {
        Quota quota;
        QuotaPieceRecord aggregatedRecord = quotaDb.getAggregatedQuota(userId, new Date());

        int numFreeDays = quotaConfig.getFreeQuotaDays();
        int numFreeAgent = quotaConfig.getFreeQuotaNumAgent();
        int numFreePipelineRun = quotaConfig.getFreeQuotaNumPipelineRun();

        if(null == aggregatedRecord){
            Date endTime = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endTime);
            cal.add(Calendar.DAY_OF_YEAR, -numFreeDays);
            Date beginTime = cal.getTime();

            List<QuotaPieceRecord> last = quotaDb.listQuota(userId, "endTime", OrderType.DESC, 1, 1);
            if(last.size() > 0){
                Date lastQuotaTime = last.get(0).getEndTime();
                if(lastQuotaTime.after(beginTime)){
                    beginTime = lastQuotaTime;
                    cal.setTime(beginTime);
                    cal.add(Calendar.DAY_OF_YEAR, numFreeDays);
                    endTime = cal.getTime();
                }
            }

            // free quota
            quota = new Quota(
                    numFreeAgent,
                    numFreePipelineRun,
                    beginTime,
                    endTime
            );
        }else{
            Date beginTime = aggregatedRecord.getBeginTime();
            Date endTime = aggregatedRecord.getEndTime();

            quota = new Quota(
                    aggregatedRecord.getMaxAgent(),
                    aggregatedRecord.getMaxPipelineRun(),
                    beginTime,
                    endTime
            );
        }

        return quota;
    }

    @Override
    public void validateQuota(Long userId) throws Exception {
        Quota quota = getCurrentQuota(userId);
        Usage usage = measureUsage(userId, quota.getBeginTime(), quota.getEndTime());

        if(usage.getNumAgent() > quota.getMaxAgent()){
            throw new Exception(String.format("number of agent [%d] exceed quota [%d]", usage.getNumAgent(), quota.getMaxAgent()));
        }

        if(usage.getNumPipelineRun() >= quota.getMaxPipelineRun()){
            throw new Exception(String.format("number of pipeline run [%d] exceed quota [%d]", usage.getNumPipelineRun(), quota.getMaxPipelineRun()));
        }

    }

    @Override
    public QuotaPieceRecord createQuota(Long userId, int maxAgent, int maxPipelineRun, Date beginTime, Date endTime, Long payQuotaTransactionId) {
        QuotaPieceRecord record = new QuotaPieceRecord();
        record.setUserId(userId);
        record.setMaxAgent(maxAgent);
        record.setMaxPipelineRun(maxPipelineRun);
        record.setBeginTime(beginTime);
        record.setEndTime(endTime);
        record.setPayQuotaTransactionId(payQuotaTransactionId);
        record.setCreateTime(new Date());

        boolean created = quotaDb.createQuota(record);
        if(!created){
            return null;
        }

        return record;

    }

    @Override
    public List<QuotaPieceRecord> listQuota(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        return quotaDb.listQuota(userId, orderField, orderType, pageNo, pageSize);
    }

    @Override
    public long countQuota(Long userId) {
        return quotaDb.countQuota(userId);
    }
}
