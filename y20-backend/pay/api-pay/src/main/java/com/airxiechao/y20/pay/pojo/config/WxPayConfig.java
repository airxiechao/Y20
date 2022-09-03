package com.airxiechao.y20.pay.pojo.config;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("pay-wxpay.yml")
public class WxPayConfig {
    private String appId;
    private String mchId;
    private String mchSerialNo;
    private String mchPrivateKeyPath;
    private String apiV3Key;
    private String notifyUrlPrefix;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchSerialNo() {
        return mchSerialNo;
    }

    public void setMchSerialNo(String mchSerialNo) {
        this.mchSerialNo = mchSerialNo;
    }

    public String getMchPrivateKeyPath() {
        return mchPrivateKeyPath;
    }

    public void setMchPrivateKeyPath(String mchPrivateKeyPath) {
        this.mchPrivateKeyPath = mchPrivateKeyPath;
    }

    public String getApiV3Key() {
        return apiV3Key;
    }

    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }

    public String getNotifyUrlPrefix() {
        return notifyUrlPrefix;
    }

    public void setNotifyUrlPrefix(String notifyUrlPrefix) {
        this.notifyUrlPrefix = notifyUrlPrefix;
    }
}
