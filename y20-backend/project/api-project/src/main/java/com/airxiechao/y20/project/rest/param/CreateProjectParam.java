package com.airxiechao.y20.project.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CreateProjectParam {

    @Required private Long userId;
    @Required private String name;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
