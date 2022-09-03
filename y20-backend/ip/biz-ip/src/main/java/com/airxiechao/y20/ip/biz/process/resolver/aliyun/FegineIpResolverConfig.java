package com.airxiechao.y20.ip.biz.process.resolver.aliyun;

import com.airxiechao.axcboot.config.annotation.Config;

@Config("ip-aliyun-fegine.yml")
public class FegineIpResolverConfig {

    private String appCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
