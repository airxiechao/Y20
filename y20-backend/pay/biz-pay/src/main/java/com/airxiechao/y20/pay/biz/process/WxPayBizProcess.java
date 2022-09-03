package com.airxiechao.y20.pay.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.pay.biz.api.IPayBiz;
import com.airxiechao.y20.pay.biz.api.IWxPayBiz;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.pojo.vo.OrderQrVo;
import com.airxiechao.y20.pay.wxpay.*;
import com.airxiechao.y20.pay.biz.wxpay.WxPayClient;
import com.airxiechao.y20.pay.db.api.IPayDb;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;
import com.airxiechao.y20.pay.pojo.config.WxPayConfig;
import com.airxiechao.y20.pay.pojo.constant.EnumPayType;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WxPayBizProcess implements IWxPayBiz {

    private static final Logger logger = LoggerFactory.getLogger(WxPayBizProcess.class);

    private IPayDb payDb = Db.get(IPayDb.class);
    private IPayBiz payBiz = Biz.get(IPayBiz.class);
    private static final WxPayConfig config = ConfigFactory.get(WxPayConfig.class);

    @Override
    public OrderQrVo orderQuotaQr(
            Long userId, String billingPlan, Integer numMonth
    ) throws Exception {

        PayPriceRecord payPriceRecord = payDb.getPayPriceByBillingPlan(billingPlan);
        if(null == payPriceRecord){
            throw new Exception("no billing plan");
        }

        double discountRate = 1.0;
        List<PayDiscountRecord> discounts = payDb.listPayDiscountByBillingPlan(billingPlan);
        for(PayDiscountRecord discount : discounts){
            if(numMonth >= discount.getNumMin() && numMonth < discount.getNumMax()){
                discountRate = discount.getRate();
                break;
            }
        }

        Integer numAgent = payPriceRecord.getNumAgent();
        Integer numPipelineRun = payPriceRecord.getNumPipelineRun();
        Integer price = payPriceRecord.getPrice();

        String outTradeNo = buildOutTradeNo(userId, billingPlan, numMonth);
        Integer total = (int)(price * numMonth * discountRate);
        NativeOrderParam param = new NativeOrderParam(
                config.getAppId(),
                config.getMchId(),
                buildDescription(billingPlan, numMonth),
                outTradeNo,
                buildAttach(userId, numAgent, numMonth, numPipelineRun, numMonth),
                getNotifyUrl(),
                total
        );

        logger.info("wxpay native order: [{}]", JSON.toJSONString(param));

        try(WxPayClient wxPayClient = new WxPayClient()){
            NativeOrderResp resp = wxPayClient.nativeOrder(param);

            PayQuotaTransactionRecord payRecord = new PayQuotaTransactionRecord(
                    userId, numAgent, numMonth, numPipelineRun, numMonth,
                    total, EnumPayType.WXPAY, outTradeNo, null);

            boolean created = payDb.insertPayQuotaTransaction(payRecord);
            if(!created){
                logger.error("create pay quota transaction record error");
            }

            String qrUrl = resp.getCode_url();
            OrderQrVo vo = new OrderQrVo(outTradeNo, qrUrl);
            return vo;
        }

    }

    @Override
    public void notifyQuotaPay(NotifyHeader header, String body) throws Exception{

        NotifyResource resource = WxPayClient.parseNotification(header, body);

        String outTradeNo = resource.getOut_trade_no();
        String transactionId = resource.getTransaction_id();
        Integer amount = resource.getAmount().getPayer_total();
        String openid = resource.getPayer().getOpenid();

        payBiz.payQuota(outTradeNo, transactionId, amount, openid);
    }

    private String buildDescription(String billingPlan, Integer numMonth){
        return String.format("配额：%s*%d月", billingPlan, numMonth);
    }

    private String buildOutTradeNo(Long userId, String billingPlan, Integer numMonth){
        return UuidUtil.random().replace("-", "");
    }

    private String buildAttach(Long userId, Integer numAgent, Integer numAgentMonth, Integer numPipelineRun, Integer numPipelineRunMonth){
        return String.format("%d,%d,%d,%d,%d", userId, numAgent, numAgentMonth, numPipelineRun, numPipelineRunMonth);
    }

    private String getNotifyUrl(){
        return String.format("%s/pay/api%s", config.getNotifyUrlPrefix(), "/service/pay/quota/notify/wxpay");
    }


}
