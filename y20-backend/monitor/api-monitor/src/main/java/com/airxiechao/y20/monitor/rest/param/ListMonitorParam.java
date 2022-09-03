package com.airxiechao.y20.monitor.rest.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;

public class ListMonitorParam {
    @Required private Long userId;
    private Long projectId;
    private String agentId;
    private String name;
    private String orderField;
    private String orderType;
    private Integer pageNo;
    private Integer pageSize;

    public ListMonitorParam() {
    }

    public ListMonitorParam(Long userId, Long projectId, String agentId, String name, String orderField, String orderType, Integer pageNo, Integer pageSize) {
        this.userId = userId;
        this.projectId = projectId;
        this.agentId = agentId;
        this.name = name;
        this.orderField = orderField;
        this.orderType = orderType;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
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
