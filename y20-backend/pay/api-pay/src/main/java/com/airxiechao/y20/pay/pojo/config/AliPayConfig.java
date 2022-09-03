package com.airxiechao.y20.pay.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("pay-alipay.yml")
public class AliPayConfig {
    private String alipayGateway;
    private String format;
    private String charset;
    private String signType;
    private String appId;
    private String merchantPrivateKey;
    private String alipayPublicKey;
    private String notifyUrlPrefix;

    public String getAlipayGateway() {
        return alipayGateway;
    }

    public void setAlipayGateway(String alipayGateway) {
        this.alipayGateway = alipayGateway;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMerchantPrivateKey() {
        return merchantPrivateKey;
    }

    public void setMerchantPrivateKey(String merchantPrivateKey) {
        this.merchantPrivateKey = merchantPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrlPrefix() {
        return notifyUrlPrefix;
    }

    public void setNotifyUrlPrefix(String notifyUrlPrefix) {
        this.notifyUrlPrefix = notifyUrlPrefix;
    }
}
