package com.airxiechao.y20.pay.pojo.vo;

import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;

import java.util.List;

public class QuotaPriceVo {
    private PayPriceRecord basic;
    private PayPriceRecord pro;
    private List<PayDiscountRecord> basicDiscounts;
    private List<PayDiscountRecord> proDiscounts;

    public QuotaPriceVo() {
    }

    public QuotaPriceVo(PayPriceRecord basic, PayPriceRecord pro, List<PayDiscountRecord> basicDiscounts, List<PayDiscountRecord> proDiscounts) {
        this.basic = basic;
        this.pro = pro;
        this.basicDiscounts = basicDiscounts;
        this.proDiscounts = proDiscounts;
    }

    public PayPriceRecord getBasic() {
        return basic;
    }

    public void setBasic(PayPriceRecord basic) {
        this.basic = basic;
    }

    public PayPriceRecord getPro() {
        return pro;
    }

    public void setPro(PayPriceRecord pro) {
        this.pro = pro;
    }

    public List<PayDiscountRecord> getBasicDiscounts() {
        return basicDiscounts;
    }

    public void setBasicDiscounts(List<PayDiscountRecord> basicDiscounts) {
        this.basicDiscounts = basicDiscounts;
    }

    public List<PayDiscountRecord> getProDiscounts() {
        return proDiscounts;
    }

    public void setProDiscounts(List<PayDiscountRecord> proDiscounts) {
        this.proDiscounts = proDiscounts;
    }
}
