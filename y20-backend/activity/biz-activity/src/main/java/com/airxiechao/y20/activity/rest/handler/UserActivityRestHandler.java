package com.airxiechao.y20.activity.rest.handler;

import com.airxiechao.axcboot.communication.common.PageData;
import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.activity.biz.api.IActivityBiz;
import com.airxiechao.y20.activity.pojo.vo.ActivityVo;
import com.airxiechao.y20.activity.rest.api.IUserActivityRest;
import com.airxiechao.y20.activity.rest.param.ListActivityParam;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserActivityRestHandler implements IUserActivityRest {

    private static final Logger logger = LoggerFactory.getLogger(UserActivityRestHandler.class);
    private IActivityBiz activityBiz = Biz.get(IActivityBiz.class);

    @Override
    public Response list(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;
        ListActivityParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, ListActivityParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }
        int pageNo = param.getPageNo();
        int pageSize = param.getPageSize();

        if(pageNo < 1){
            return new Response().error("pageNo must >= 1");
        }

        if(pageSize > 50){
            return new Response().error("pageSize must <= 50");
        }

        List<ActivityVo> list = activityBiz.list(param.getUserId(), pageNo, pageSize).stream()
                .map(document -> new ActivityVo(document)).collect(Collectors.toList());

        return new Response().data(new PageData(
                pageNo,
                pageSize,
                null,
                list
        ));
    }
}
