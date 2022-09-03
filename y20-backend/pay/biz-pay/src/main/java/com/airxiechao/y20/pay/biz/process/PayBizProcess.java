package com.airxiechao.y20.pay.biz.process;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.y20.common.core.db.Db;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.pay.biz.api.IPayBiz;
import com.airxiechao.y20.pay.db.api.IPayDb;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;
import com.airxiechao.y20.pay.pojo.constant.EnumPayPriceBillingPlan;
import com.airxiechao.y20.quota.rest.api.IServiceQuotaRest;
import com.airxiechao.y20.quota.rest.param.ServiceCreateQuotaParam;

import java.util.Date;
import java.util.List;

public class PayBizProcess implements IPayBiz {

    private IPayDb payDb = Db.get(IPayDb.class);

    @Override
    public PayPriceRecord getBillingPlanPrice(String billingPlan) {
        PayPriceRecord payPriceRecord = payDb.getPayPriceByBillingPlan(billingPlan);
        return  payPriceRecord;
    }

    @Override
    public List<PayDiscountRecord> listBillingPlanDiscount(String billingPlan) {
        List<PayDiscountRecord> payDiscountRecords = payDb.listPayDiscountByBillingPlan(billingPlan);
        return  payDiscountRecords;
    }

    @Override
    public PayQuotaTransactionRecord getPayQuotaTransactionByUserIdAndOutTradeNo(Long userId, String outTradeNo) {
        PayQuotaTransactionRecord transactionRecord = payDb.getPayQuotaTransactionByUserIdAndOutTradeNo(userId, outTradeNo);
        return transactionRecord;
    }

    @Override
    public void payQuota(String outTradeNo, String transactionId, Integer payAmount, String payer) throws Exception {

        // update pay quota transaction
        PayQuotaTransactionRecord payRecord = payDb.getPayQuotaTransactionByOutTradeNo(outTradeNo);
        if(null == payRecord){
            throw new Exception("no pay quota transaction");
        }

        if(payRecord.getPayed()){
            throw new Exception("pay quota transaction already payed");
        }

        payRecord.setTransactionId(transactionId);
        payRecord.setPayAmount(payAmount);
        payRecord.setPayed(true);
        payRecord.setPayer(payer);
        payRecord.setPayTime(new Date());

        boolean updated = payDb.updatePayQuotaTransaction(payRecord);
        if(!updated){
            throw new Exception("update pay quota transaction error");
        }

        // create quota
        Response resp = ServiceRestClient.get(IServiceQuotaRest.class).createQuota(new ServiceCreateQuotaParam(
                payRecord.getUserId(), payRecord.getNumAgent(), payRecord.getNumAgentMonth(),
                payRecord.getNumPipelineRun(), payRecord.getNumPipelineRunMonth(),  payRecord.getId()
        ));
        if(!resp.isSuccess()){
            throw new Exception("create quota error");
        }
    }
}
