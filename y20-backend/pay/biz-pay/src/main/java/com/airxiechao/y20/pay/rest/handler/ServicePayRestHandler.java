package com.airxiechao.y20.pay.rest.handler;

import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.pay.biz.api.IAliPayBiz;
import com.airxiechao.y20.pay.biz.api.IWxPayBiz;
import com.airxiechao.y20.pay.wxpay.NotifyHeader;
import com.airxiechao.y20.pay.wxpay.NotifyResp;
import com.airxiechao.y20.pay.rest.api.IServicePayRest;
import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class ServicePayRestHandler implements IServicePayRest {

    private static final Logger logger = LoggerFactory.getLogger(ServicePayRestHandler.class);

    private IWxPayBiz wxPayBiz = Biz.get(IWxPayBiz.class);
    private IAliPayBiz aliPayBiz = Biz.get(IAliPayBiz.class);

    @Override
    public NotifyResp wxpayNotifyQuotaPay(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        String serial = RestUtil.header(exchange, "Wechatpay-Serial");
        String nonce = RestUtil.header(exchange, "Wechatpay-Nonce");
        String signature = RestUtil.header(exchange, "Wechatpay-Signature");
        String timestamp = RestUtil.header(exchange, "Wechatpay-Timestamp");
        NotifyHeader header = new NotifyHeader(serial, nonce, signature, timestamp);

        String body = RestUtil.rawStringData(exchange, StandardCharsets.UTF_8);
        logger.info("wxpay notify quota pay: header [{}], body [{}]",
                JSON.toJSONString(header), body);

        try {
            wxPayBiz.notifyQuotaPay(header, body);
            logger.info("wxpay notify quota pay success");
        } catch (Exception e) {
            logger.error("wxpay notify quota pay error", e);
        }

        return new NotifyResp("SUCCESS", "成功");
    }

    @Override
    public void alipayNotifyQuotaPay(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        Map<String, String> params = RestUtil.allFormStringData(exchange);
        logger.info("alipay notify quota pay: [{}]", JSON.toJSONString(params));

        try {
            aliPayBiz.notifyQuotaPay(params);
            logger.info("alipay notify quota pay success");
        } catch (Exception e) {
            logger.error("alipay notify quota pay error", e);
        }

        exchange.getResponseSender().send("success");
    }
}
