package com.airxiechao.y20.quota.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ServiceValidateQuotaParam {
    @Required private Long userId;

    public ServiceValidateQuotaParam() {
    }

    public ServiceValidateQuotaParam(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
