package com.airxiechao.y20.pay.wxpay;

public class NotifyResp {
    private String code;
    private String message;

    public NotifyResp() {
    }

    public NotifyResp(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
