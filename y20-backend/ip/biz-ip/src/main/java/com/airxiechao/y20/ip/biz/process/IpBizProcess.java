package com.airxiechao.y20.ip.biz.process;

import com.airxiechao.y20.ip.biz.api.IIpBiz;
import com.airxiechao.y20.ip.biz.process.resolver.IIpResolver;
import com.airxiechao.y20.ip.biz.process.resolver.aliyun.FegineIpResolver;
import com.airxiechao.y20.ip.pojo.IpLocation;

public class IpBizProcess implements IIpBiz {

    private IIpResolver ipResolver = new FegineIpResolver();

    @Override
    public IpLocation resolve(String ip) throws Exception {
        return ipResolver.resolve(ip);
    }
}
