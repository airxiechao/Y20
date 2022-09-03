package com.airxiechao.y20.pay.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;

import java.util.List;

@IDb("mybatis-y20-pay.xml")
public interface IPayDb {
    PayPriceRecord getPayPriceByBillingPlan(String billingPlan);
    List<PayDiscountRecord> listPayDiscountByBillingPlan(String billingPlan);
    PayQuotaTransactionRecord getPayQuotaTransactionByOutTradeNo(String outTradeNo);
    PayQuotaTransactionRecord getPayQuotaTransactionByUserIdAndOutTradeNo(Long userId, String outTradeNo);
    boolean insertPayQuotaTransaction(PayQuotaTransactionRecord payQuotaTransactionRecord);
    boolean updatePayQuotaTransaction(PayQuotaTransactionRecord payQuotaTransactionRecord);
}
