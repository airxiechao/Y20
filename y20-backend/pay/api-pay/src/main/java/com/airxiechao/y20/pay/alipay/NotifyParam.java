package com.airxiechao.y20.pay.alipay;

public class NotifyParam {
    private String outTradeNo;
    private String tradeNo;
    private String tradeStatus;
    private String buyerId;
    private Double totalAmount;

    public NotifyParam(String outTradeNo, String tradeNo, String tradeStatus, String buyerId, Double totalAmount) {
        this.outTradeNo = outTradeNo;
        this.tradeNo = tradeNo;
        this.tradeStatus = tradeStatus;
        this.buyerId = buyerId;
        this.totalAmount = totalAmount;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
