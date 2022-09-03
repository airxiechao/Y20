package com.airxiechao.y20.pay.biz.alipay;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.pay.alipay.NotifyParam;
import com.airxiechao.y20.pay.alipay.PageOrderParam;
import com.airxiechao.y20.pay.pojo.config.AliPayConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AliPayClient {
    private static final Logger logger = LoggerFactory.getLogger(AliPayClient.class);
    private static final AliPayConfig config = ConfigFactory.get(AliPayConfig.class);

    private AlipayClient client;

    public AliPayClient() {
        this.client = new DefaultAlipayClient(
            config.getAlipayGateway(),
            config.getAppId(),
            config.getMerchantPrivateKey(),
            config.getFormat(),
            config.getCharset(),
            config.getAlipayPublicKey(),
            config.getSignType()
        );
    }

    public AlipayTradePagePayResponse pageOrder(PageOrderParam param) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(param.getNotifyUrl());
        request.setReturnUrl(param.getReturnUrl());

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", param.getOutTradeNo());
        bizContent.put("total_amount", param.getTotalAmount());
        bizContent.put("subject", param.getSubject());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        String strBizContent = JSON.toJSONString(bizContent);
        logger.info("alipay page order: [{}]", strBizContent);

        request.setBizContent(strBizContent);
        AlipayTradePagePayResponse response = client.pageExecute(request);
        logger.info("alipay page order -> [{}]", JSON.toJSONString(response));

        return response;
    }

    public static NotifyParam parseNotification(Map<String, String> params) throws Exception {
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                config.getAlipayPublicKey(),
                config.getCharset(),
                config.getSignType()
        );

        if(!signVerified) {
            throw new Exception("alipay notification sign error");
        }

        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String buyerId = params.get("buyer_id");
        Double totalAmount = Double.valueOf(params.get("total_amount"));
        if(!tradeStatus.equals("TRADE_FINISHED") && !tradeStatus.equals("TRADE_SUCCESS")){
            throw new Exception("alipay notification trade not complete");
        }

        return new NotifyParam(outTradeNo, tradeNo, tradeStatus, buyerId, totalAmount);

    }
}
