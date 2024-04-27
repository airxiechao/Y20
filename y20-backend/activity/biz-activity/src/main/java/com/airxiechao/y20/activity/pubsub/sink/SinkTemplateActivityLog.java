package com.airxiechao.y20.activity.pubsub.sink;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.activity.biz.api.IActivityBiz;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateActionType;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateEventSinkName;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateEventType;
import com.airxiechao.y20.template.pojo.pubsub.event.EventTemplateActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Sink(
        events = { EnumTemplateEventType.TEMPLATE_ACTIVITY +".*" },
        name = EnumTemplateEventSinkName.TEMPLATE_ACTIVITY_LOG_SINK)
public class SinkTemplateActivityLog implements ISubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SinkTemplateActivityLog.class);
    private IActivityBiz activityBiz = Biz.get(IActivityBiz.class);

    @Override
    public Response handle(Map<String, Object> params) throws Exception {
        EventTemplateActivity event = ModelUtil.fromMap(params, EventTemplateActivity.class);
        Long userId = event.getUserId();
        Long templateId = event.getTemplateId();
        String actionType = event.getActionType();
        String actionParam = event.getActionParam();

        logger.info("template [{}] activity [{}]", templateId, actionType);

        switch (actionType){
            case EnumTemplateActionType.SEARCH:
            case EnumTemplateActionType.CLICK:
            case EnumTemplateActionType.APPLY:
                activityBiz.createTemplateActivity(userId, templateId, actionType, actionParam);
                break;
        }

        return new Response();
    }
}
