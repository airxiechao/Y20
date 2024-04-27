package com.airxiechao.y20.pay.biz.process;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.pay.alipay.NotifyParam;
import com.airxiechao.y20.pay.alipay.PageOrderParam;
import com.airxiechao.y20.pay.biz.alipay.AliPayClient;
import com.airxiechao.y20.pay.biz.api.IAliPayBiz;
import com.airxiechao.y20.pay.biz.api.IPayBiz;
import com.airxiechao.y20.pay.db.api.IPayDb;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;
import com.airxiechao.y20.pay.pojo.config.AliPayConfig;
import com.airxiechao.y20.pay.pojo.constant.EnumPayType;
import com.airxiechao.y20.pay.pojo.vo.OrderPageVo;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliPayBizProcess implements IAliPayBiz {

    private static final Logger logger = LoggerFactory.getLogger(AliPayBizProcess.class);

    private static final AliPayConfig config = ConfigFactory.get(AliPayConfig.class);

    private IPayDb payDb = Db.get(IPayDb.class);
    private IPayBiz payBiz = Biz.get(IPayBiz.class);

    @Override
    public OrderPageVo orderQuotaPage(Long userId, String billingPlan, Integer numMonth) throws Exception {
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

        PageOrderParam pageOrderParam = new PageOrderParam(
                outTradeNo,
                buildDescription(billingPlan, numMonth),
                total / 100.0,
                getNotifyUrl(),
                getReturnUrl()
        );

        AliPayClient client = new AliPayClient();
        AlipayTradePagePayResponse response = client.pageOrder(pageOrderParam);
        if(!response.isSuccess()){
            logger.error(response.getMsg());
            throw new Exception("alipay page order error");
        }

        PayQuotaTransactionRecord payRecord = new PayQuotaTransactionRecord(
                userId, numAgent, numMonth, numPipelineRun, numMonth,
                total, EnumPayType.ALIPAY, outTradeNo, null);

        boolean created = payDb.insertPayQuotaTransaction(payRecord);
        if(!created){
            logger.error("create pay quota transaction record error");
        }

        String html = response.getBody();

        String action;
        Matcher actionMat = Pattern.compile("action=\"([^\"]+)\"").matcher(html);
        if(!actionMat.find()){
            throw new Exception("response has no form action");
        }
        action = actionMat.group(1);

        String bizContent;
        Matcher bizContentMat = Pattern.compile("name=\"biz_content\" value=\"([^\"]+)\"").matcher(html);
        if(!bizContentMat.find()){
            throw new Exception("response has no biz content");
        }
        bizContent = bizContentMat.group(1).replace("&quot;", "\"");

        OrderPageVo orderPageVo = new OrderPageVo(outTradeNo, action, bizContent);
        return orderPageVo;
    }

    @Override
    public void notifyQuotaPay(Map<String, String> params) throws Exception {
        NotifyParam notifyParam = AliPayClient.parseNotification(params);

        String outTradeNo = notifyParam.getOutTradeNo();
        String transactionId = notifyParam.getTradeNo();
        String buyerId = notifyParam.getBuyerId();
        Integer amount = (int)Math.round(notifyParam.getTotalAmount() * 100);

        payBiz.payQuota(outTradeNo, transactionId, amount, buyerId);
    }

    private String buildDescription(String billingPlan, Integer numMonth){
        return String.format("Y20配额：%s*%d月", billingPlan, numMonth);
    }

    private String buildOutTradeNo(Long userId, String billingPlan, Integer numMonth){
        return UuidUtil.random().replace("-", "");
    }

    private String getReturnUrl(){
        return String.format("%s%s", config.getNotifyUrlPrefix(), "/nav/user/billing/quota");
    }

    private String getNotifyUrl(){
        return String.format("%s/pay/api%s", config.getNotifyUrlPrefix(), "/service/pay/quota/notify/alipay");
    }
}
