package com.airxiechao.y20.pay.wxpay;

public class NotifyHeader {
    private String serial;
    private String nonce;
    private String signature;
    private String timestamp;

    public NotifyHeader() {
    }

    public NotifyHeader(String serial, String nonce, String signature, String timestamp) {
        this.serial = serial;
        this.nonce = nonce;
        this.signature = signature;
        this.timestamp = timestamp;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
