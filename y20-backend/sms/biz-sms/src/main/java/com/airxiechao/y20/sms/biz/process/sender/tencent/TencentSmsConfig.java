package com.airxiechao.y20.sms.biz.process.sender.tencent;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("sms-tencent.yml")
public class TencentSmsConfig {
    private String secretId;
    private String secretKey;
    private String sdkAppId;
    private String signName;
    private String verificationCodeTemplateId;
    private String monitorAlertTemplateId;

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getVerificationCodeTemplateId() {
        return verificationCodeTemplateId;
    }

    public void setVerificationCodeTemplateId(String verificationCodeTemplateId) {
        this.verificationCodeTemplateId = verificationCodeTemplateId;
    }

    public String getMonitorAlertTemplateId() {
        return monitorAlertTemplateId;
    }

    public void setMonitorAlertTemplateId(String monitorAlertTemplateId) {
        this.monitorAlertTemplateId = monitorAlertTemplateId;
    }
}
