package com.airxiechao.y20.pay.alipay;

public class PageOrderParam {

    private String outTradeNo;
    private String subject;
    private double totalAmount;
    private String notifyUrl;
    private String returnUrl;

    public PageOrderParam(String outTradeNo, String subject, double totalAmount, String notifyUrl, String returnUrl) {
        this.outTradeNo = outTradeNo;
        this.subject = subject;
        this.totalAmount = totalAmount;
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUrl;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
