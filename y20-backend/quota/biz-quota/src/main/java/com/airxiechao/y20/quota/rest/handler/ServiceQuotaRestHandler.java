package com.airxiechao.y20.quota.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.quota.biz.api.IQuotaBiz;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;
import com.airxiechao.y20.quota.rest.api.IServiceQuotaRest;
import com.airxiechao.y20.quota.rest.param.ServiceCreateQuotaParam;
import com.airxiechao.y20.quota.rest.param.ServiceValidateQuotaParam;
import io.undertow.server.HttpServerExchange;

import java.util.Calendar;
import java.util.Date;

public class ServiceQuotaRestHandler implements IServiceQuotaRest {

    private static final Integer DAYS_OF_MONTH = 31;
    private IQuotaBiz quotaBiz = Biz.get(IQuotaBiz.class);

    @Override
    public Response validateQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceValidateQuotaParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceValidateQuotaParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        try {
            quotaBiz.validateQuota(param.getUserId());
            return new Response();
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response createQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange)exc;

        ServiceCreateQuotaParam param;
        try {
            param = RestUtil.rawJsonData(exchange, ServiceCreateQuotaParam.class);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }

        Date beginTime = new Date();
        Calendar cal = Calendar.getInstance();

        if(param.getNumAgentMonth() == param.getNumPipelineRunMonth()){
            // add quota
            for(int i = 0; i < param.getNumPipelineRunMonth(); ++i){
                cal.setTime(beginTime);
                cal.add(Calendar.DAY_OF_YEAR, DAYS_OF_MONTH);
                Date endTime = cal.getTime();
                quotaBiz.createQuota(
                        param.getUserId(), param.getNumAgent(), param.getNumPipelineRun(),
                        beginTime, endTime, param.getPayQuotaTransactionId());

                beginTime = endTime;
            }
        }else{
            // add agent quota
            if(param.getNumAgent() * param.getNumAgentMonth() > 0){
                cal.setTime(beginTime);
                cal.add(Calendar.DAY_OF_YEAR, DAYS_OF_MONTH * param.getNumAgentMonth());
                Date endTime = cal.getTime();
                quotaBiz.createQuota(
                        param.getUserId(), param.getNumAgent(), 0,
                        beginTime, endTime, param.getPayQuotaTransactionId());
            }

            // add pipeline run quota
            if(param.getNumPipelineRun() * param.getNumPipelineRunMonth() > 0){
                for(int i = 0; i < param.getNumPipelineRunMonth(); ++i){
                    cal.setTime(beginTime);
                    cal.add(Calendar.DAY_OF_YEAR, DAYS_OF_MONTH);
                    Date endTime = cal.getTime();
                    quotaBiz.createQuota(
                            param.getUserId(), 0, param.getNumPipelineRun(),
                            beginTime, endTime, param.getPayQuotaTransactionId());

                    beginTime = endTime;
                }

            }
        }

        return new Response();
    }
}
