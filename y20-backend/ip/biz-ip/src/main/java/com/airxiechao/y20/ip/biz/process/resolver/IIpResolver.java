package com.airxiechao.y20.ip.biz.process.resolver;

import com.airxiechao.y20.ip.pojo.IpLocation;

public interface IIpResolver {
    IpLocation resolve(String ip) throws Exception;
}
