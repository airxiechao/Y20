package com.airxiechao.y20.ip.biz.process.resolver.aliyun;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.HttpUtil;
import com.airxiechao.y20.ip.biz.process.resolver.IIpResolver;
import com.airxiechao.y20.ip.pojo.IpLocation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class FegineIpResolver implements IIpResolver {

    private static final Logger logger = LoggerFactory.getLogger(FegineIpResolver.class);
    private static final FegineIpResolverConfig config = ConfigFactory.get(FegineIpResolverConfig.class);
    private static final String URL = "https://ips.market.alicloudapi.com/iplocaltion";
    private static final Integer TIMEOUT_SECS = 5;

    @Override
    public IpLocation resolve(String ip) throws Exception {
        String appCode = config.getAppCode();

        String ret = HttpUtil.get(URL, new HashMap<>(){{
            put("ip", ip);
        }}, new HashMap<>(){{
            put("Authorization", String.format("APPCODE %s", appCode));
        }}, null, TIMEOUT_SECS, true);

        JSONObject json = JSON.parseObject(ret);
        Integer code = json.getInteger("code");
        String message = json.getString("message");
        if(code != 100){
            logger.error("ip resolve error: {}", message);
            throw new Exception(message);
        }

        JSONObject result = json.getJSONObject("result");
        if(null == result){
            logger.error("ip resolve error: no result");
            throw new Exception("no ip result");
        }

        IpLocation ipLocation = new IpLocation(
                result.getString("nation"),
                result.getString("province"),
                result.getString("city"),
                result.getString("district")
        );

        return ipLocation;
    }
}
