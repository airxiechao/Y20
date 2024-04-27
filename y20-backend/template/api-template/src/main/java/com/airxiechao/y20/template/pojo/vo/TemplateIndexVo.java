package com.airxiechao.y20.template.pojo.vo;

import com.airxiechao.axcboot.search.annotation.IndexField;
import com.airxiechao.y20.template.pojo.Template;

public class TemplateIndexVo {
    @IndexField private String templateId;
    @IndexField private String userId;
    @IndexField(isText = true) private String text;

    public TemplateIndexVo(Template template) {
        this.templateId = template.getTemplateId()+"";
        this.userId = template.getUserId()+"";
        this.text = String.format("%s %s", template.getName(), template.getDescription());
    }
}
