package com.airxiechao.y20.pay.pojo.vo;

public class OrderPageVo {
    private String outTradeNo;
    private String action;
    private String bizContent;

    public OrderPageVo() {
    }

    public OrderPageVo(String outTradeNo, String action, String bizContent) {
        this.outTradeNo = outTradeNo;
        this.action = action;
        this.bizContent = bizContent;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBizContent() {
        return bizContent;
    }

    public void setBizContent(String bizContent) {
        this.bizContent = bizContent;
    }
}
