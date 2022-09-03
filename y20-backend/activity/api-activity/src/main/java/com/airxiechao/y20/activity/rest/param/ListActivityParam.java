package com.airxiechao.y20.activity.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ListActivityParam {
    @Required private Long userId;
    @Required private Integer pageNo;
    @Required private Integer pageSize;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
