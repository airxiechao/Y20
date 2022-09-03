package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class CountMonitorStatusParam {
    @Required private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
