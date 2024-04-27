package com.airxiechao.y20.template.pubsub.sink;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.pubsub.ISubscriber;
import com.airxiechao.axcboot.communication.pubsub.annotation.Sink;
import com.airxiechao.axcboot.util.ModelUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.template.biz.api.ITemplateBiz;
import com.airxiechao.y20.template.db.record.TemplateRecord;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateActionType;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateEventSinkName;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateEventType;
import com.airxiechao.y20.template.pojo.pubsub.event.EventTemplateActivity;
import com.airxiechao.y20.template.search.TemplateIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Sink(
        events = { EnumTemplateEventType.TEMPLATE_ACTIVITY +".*" },
        name = EnumTemplateEventSinkName.TEMPLATE_ACTIVITY_INDEX_SINK)
public class SinkTemplateActivityIndex implements ISubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SinkTemplateActivityIndex.class);
    private ITemplateBiz templateBiz = Biz.get(ITemplateBiz.class);

    @Override
    public Response handle(Map<String, Object> params) throws Exception {
        EventTemplateActivity event = ModelUtil.fromMap(params, EventTemplateActivity.class);
        Long userId = event.getUserId();
        Long templateId = event.getTemplateId();
        String actionType = event.getActionType();
        String actionParam = event.getActionParam();

        logger.info("template [{}] activity [{}]", templateId, actionType);

        TemplateRecord record = null;
        switch (actionType){
            case EnumTemplateActionType.ADD:
                record = templateBiz.get(userId, templateId);
                TemplateIndex.getInstance().add(record.toPojo());
                break;
            case EnumTemplateActionType.UPDATE:
                record = templateBiz.get(userId, templateId);
                TemplateIndex.getInstance().update(record.toPojo());
                break;
            case EnumTemplateActionType.DELETE:
                TemplateIndex.getInstance().delete(templateId);
                break;
        }

        return new Response();
    }
}
