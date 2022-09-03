package com.airxiechao.y20.pay.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;

import java.util.List;

@IBiz
public interface IPayBiz {
    PayPriceRecord getBillingPlanPrice(String billingPlan);
    List<PayDiscountRecord> listBillingPlanDiscount(String billingPlan);
    PayQuotaTransactionRecord getPayQuotaTransactionByUserIdAndOutTradeNo(Long userId, String outTradeNo);
    void payQuota(String outTradeNo, String transactionId, Integer payAmount, String payer) throws Exception;
}
