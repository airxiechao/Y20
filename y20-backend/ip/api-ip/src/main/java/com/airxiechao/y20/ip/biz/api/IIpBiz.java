package com.airxiechao.y20.ip.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.ip.pojo.IpLocation;

@IBiz
public interface IIpBiz {
    IpLocation resolve(String ip) throws Exception;
}
