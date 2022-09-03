package com.airxiechao.y20.pay.biz.wxpay;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.JavaResourceFs;
import com.airxiechao.y20.pay.pojo.config.WxPayConfig;
import com.airxiechao.y20.pay.wxpay.*;
import com.alibaba.fastjson.JSON;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

public class WxPayClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(WxPayClient.class);
    private static final WxPayConfig config = ConfigFactory.get(WxPayConfig.class);
    private static Verifier verifier;
    private static WechatPayHttpClientBuilder wechatPayHttpClientBuilder;

    static {

        try {
            PrivateKey merchantPrivateKey;

            IFs fs = new JavaResourceFs();
            try(InputStream inputStream = fs.getInputStream(config.getMchPrivateKeyPath())) {
                merchantPrivateKey = PemUtil.loadPrivateKey(inputStream);
            }

            // 获取证书管理器实例
            CertificatesManager certificatesManager = CertificatesManager.getInstance();
            certificatesManager.putMerchant(config.getMchId(), new WechatPay2Credentials(config.getMchId(),
                    new PrivateKeySigner(config.getMchSerialNo(), merchantPrivateKey)), config.getApiV3Key().getBytes(StandardCharsets.UTF_8));

            // 从证书管理器中获取verifier
            verifier = certificatesManager.getVerifier(config.getMchId());

            wechatPayHttpClientBuilder = WechatPayHttpClientBuilder.create()
                    .withMerchant(config.getMchId(), config.getMchSerialNo(), merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(verifier));

        } catch (Exception e) {
            logger.error("init wxpay client error", e);
            throw new RuntimeException(e);
        }
    }

    private CloseableHttpClient httpClient;

    public WxPayClient(){
        httpClient = wechatPayHttpClientBuilder.build();
    }

    public NativeOrderResp nativeOrder(NativeOrderParam param) throws Exception {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");

        StringEntity entity = new StringEntity(JSON.toJSONString(param),"utf-8");
        entity.setContentType("application/json");

        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String resp = EntityUtils.toString(response.getEntity());
                logger.info("wxpay native order success -> [{}]", resp);
                return JSON.parseObject(resp, NativeOrderResp.class);
            } else {
                logger.info("wxpay native order fail -> [{}], [{}]", statusCode, EntityUtils.toString(response.getEntity()));
                throw new Exception("wxpay native order error");
            }
        } finally {
            response.close();
        }
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }


    public static NotifyResource parseNotification(NotifyHeader header, String body) throws Exception {
        NotificationRequest request = new NotificationRequest.Builder()
                .withSerialNumber(header.getSerial())
                .withNonce(header.getNonce())
                .withTimestamp(header.getTimestamp())
                .withSignature(header.getSignature())
                .withBody(body)
                .build();

        NotificationHandler handler = new NotificationHandler(verifier, config.getApiV3Key().getBytes(StandardCharsets.UTF_8));

        // 验签和解析请求体
        Notification notification = handler.parse(request);

        NotifyResource resource = JSON.parseObject(notification.getDecryptData(), NotifyResource.class);
        return resource;
    }
}
