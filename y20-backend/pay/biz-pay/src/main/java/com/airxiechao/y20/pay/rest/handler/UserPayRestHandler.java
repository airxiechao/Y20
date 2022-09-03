package com.airxiechao.y20.pay.rest.handler;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.y20.common.core.biz.Biz;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.pay.biz.api.IAliPayBiz;
import com.airxiechao.y20.pay.biz.api.IPayBiz;
import com.airxiechao.y20.pay.biz.api.IWxPayBiz;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;
import com.airxiechao.y20.pay.pojo.constant.EnumPayPriceBillingPlan;
import com.airxiechao.y20.pay.pojo.constant.EnumPayType;
import com.airxiechao.y20.pay.pojo.vo.OrderPageVo;
import com.airxiechao.y20.pay.pojo.vo.OrderQrVo;
import com.airxiechao.y20.pay.pojo.vo.QuotaPriceVo;
import com.airxiechao.y20.pay.rest.api.IUserPayRest;
import com.airxiechao.y20.pay.rest.param.GetQuotaOrderParam;
import com.airxiechao.y20.pay.rest.param.GetQuotaPriceParam;
import com.airxiechao.y20.pay.rest.param.OrderQuotaQrParam;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserPayRestHandler implements IUserPayRest {

    private static final Logger logger = LoggerFactory.getLogger(UserPayRestHandler.class);

    private IPayBiz payBiz = Biz.get(IPayBiz.class);
    private IWxPayBiz wxPayBiz = Biz.get(IWxPayBiz.class);
    private IAliPayBiz aliPayBiz = Biz.get(IAliPayBiz.class);

    @Override
    public Response getQuotaPrice(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetQuotaPriceParam param = null;
        try {
            param = RestUtil.queryData(exchange, GetQuotaPriceParam.class);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        // price
        PayPriceRecord basicPrice = payBiz.getBillingPlanPrice(EnumPayPriceBillingPlan.BASIC);
        PayPriceRecord proPrice = payBiz.getBillingPlanPrice(EnumPayPriceBillingPlan.PRO);

        // discount
        List<PayDiscountRecord> basicDiscounts = payBiz.listBillingPlanDiscount(EnumPayPriceBillingPlan.BASIC);
        List<PayDiscountRecord> proDiscounts = payBiz.listBillingPlanDiscount(EnumPayPriceBillingPlan.PRO);

        QuotaPriceVo vo = new QuotaPriceVo(basicPrice, proPrice, basicDiscounts, proDiscounts);

        return new Response().data(vo);
    }

    @Override
    public Response orderQuota(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        OrderQuotaQrParam param = null;
        try {
            param = EnhancedRestUtil.rawJsonDataWithHeader(exchange, OrderQuotaQrParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        int numMonth = param.getNumMonth();
        if(numMonth < 0 || numMonth > 12){
            return new Response().error("numMonth must be between 1 and 12");
        }

        String payType = param.getPayType();
        switch (payType){
            case EnumPayType.WXPAY:
                try {
                    OrderQrVo orderQrVo = wxPayBiz.orderQuotaQr(
                            param.getUserId(),
                            param.getBillingPlan(),
                            numMonth);
                    return new Response().data(orderQrVo);
                } catch (Exception e) {
                    logger.error("wxpay order quota qr error", e);
                    return new Response().error(e.getMessage());
                }
            case EnumPayType.ALIPAY:
                try {
                    OrderPageVo orderPageVo = aliPayBiz.orderQuotaPage(
                            param.getUserId(),
                            param.getBillingPlan(),
                            numMonth);
                    return new Response().data(orderPageVo);
                } catch (Exception e) {
                    logger.error("alipay order quota qr error", e);
                    return new Response().error(e.getMessage());
                }
            default:
                return new Response().error(String.format("pay type [%s] not support", payType));
        }


    }

    @Override
    public Response getQuotaOrder(Object exc) {
        HttpServerExchange exchange = (HttpServerExchange) exc;

        GetQuotaOrderParam param = null;
        try {
            param = EnhancedRestUtil.queryDataWithHeader(exchange, GetQuotaOrderParam.class, true);
        } catch (Exception e) {
            logger.error("parse rest param error", e);
            return new Response().error(e.getMessage());
        }

        PayQuotaTransactionRecord transactionRecord = payBiz.getPayQuotaTransactionByUserIdAndOutTradeNo(
                param.getUserId(), param.getOutTradeNo());

        if(null == transactionRecord){
            return new Response().error();
        }

        return new Response().data(transactionRecord);
    }

}
