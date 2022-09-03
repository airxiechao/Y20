package com.airxiechao.y20.pay.pojo.vo;

public class OrderQrVo {
    private String outTradeNo;
    private String qrUrl;

    public OrderQrVo() {
    }

    public OrderQrVo(String outTradeNo, String qrUrl) {
        this.outTradeNo = outTradeNo;
        this.qrUrl = qrUrl;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}
