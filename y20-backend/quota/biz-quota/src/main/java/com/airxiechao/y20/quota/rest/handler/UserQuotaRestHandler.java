package com.airxiechao.y20.quota.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.quota.biz.api.IQuotaBiz;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;
import com.airxiechao.y20.quota.pojo.Quota;
import com.airxiechao.y20.quota.pojo.Usage;
import com.airxiechao.y20.quota.pojo.vo.FreeQuotaVo;
import com.airxiechao.y20.quota.rest.api.IUserQuotaRest;
import com.airxiechao.y20.quota.rest.param.GetCurrentQuotaParam;
import com.airxiechao.y20.quota.rest.param.GetFreeQuotaParam;
import com.airxiechao.y20.quota.rest.param.ListQuotaParam;
import com.airxiechao.y20.quota.rest.param.GetQuotaUsageParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserQuotaRestHandler implements IUserQuotaRest {

    private static final Logger logger = LoggerFactory.getLogger(UserQuotaRestHandler.class);

    private IQuotaBiz quotaBiz = Biz.get(IQuotaBiz.class);

    @Override
    public Response getFreeQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetFreeQuotaParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetFreeQuotaParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        FreeQuotaVo vo = quotaBiz.getFreeQuota();

        return new Response().data(vo);
    }

    @Override
    public Response getQuotaUsage(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetQuotaUsageParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetQuotaUsageParam.class, false);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Quota quota = quotaBiz.getCurrentQuota(param.getUserId());

        try {
            Usage usage = quotaBiz.measureUsage(param.getUserId(), quota.getBeginTime(), quota.getEndTime());
            return new Response().data(usage);
        } catch (Exception e) {
            return new Response().error(e.getMessage());
        }
    }

    @Override
    public Response getCurrentQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetCurrentQuotaParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetCurrentQuotaParam.class, false);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        Quota quota = quotaBiz.getCurrentQuota(param.getUserId());
        return new Response().data(quota);
    }

    @Override
    public Response listQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        ListQuotaParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListQuotaParam.class, false);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        List<QuotaPieceRecord> list = quotaBiz.listQuota(
                param.getUserId(),
                param.getOrderField(),
                param.getOrderType(),
                param.getPageNo(),
                param.getPageSize());

        long count = quotaBiz.countQuota(param.getUserId());

        return new Response().data(new PageData(
                param.getPageNo(),
                param.getPageSize(),
                count,
                list));
    }
}
