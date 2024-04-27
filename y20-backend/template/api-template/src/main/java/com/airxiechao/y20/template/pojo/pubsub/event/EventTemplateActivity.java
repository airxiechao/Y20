package com.airxiechao.y20.template.pojo.pubsub.event;

import com.airxiechao.y20.common.pubsub.event.Event;
import com.airxiechao.y20.template.pojo.constant.EnumTemplateEventType;

public class EventTemplateActivity extends Event {
    public static String type(Long templateId){
        return EnumTemplateEventType.TEMPLATE_ACTIVITY+"."+(null != templateId ? templateId : "*");
    }

    private Long userId;
    private Long templateId;
    private String actionType;
    private String actionParam;

    public EventTemplateActivity() {
    }

    public EventTemplateActivity(
            Long userId,
            Long templateId,
            String actionType,
            String actionParam
    ) {
        setType(type(templateId));

        this.userId = userId;
        this.templateId = templateId;
        this.actionType = actionType;
        this.actionParam = actionParam;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }
}
