package com.airxiechao.y20.pay.db.procedure;

import com.airxiechao.axcboot.core.db.AbstractDbProcedure;
import com.airxiechao.axcboot.storage.db.sql.DbManager;
import com.airxiechao.axcboot.storage.db.sql.model.SqlParams;
import com.airxiechao.axcboot.storage.db.sql.util.DbUtil;
import com.airxiechao.axcboot.storage.db.sql.util.SqlParamsBuilder;
import com.airxiechao.y20.pay.db.api.IPayDb;
import com.airxiechao.y20.pay.db.record.PayDiscountRecord;
import com.airxiechao.y20.pay.db.record.PayPriceRecord;
import com.airxiechao.y20.pay.db.record.PayQuotaTransactionRecord;

import java.util.List;

public class PayDbProcedure extends AbstractDbProcedure implements IPayDb {

    public PayDbProcedure(DbManager dbManager) {
        super(dbManager);
    }

    @Override
    public PayPriceRecord getPayPriceByBillingPlan(String billingPlan) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PayPriceRecord.class))
                .where(DbUtil.column(PayPriceRecord.class, "billingPlan"), "=", billingPlan);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PayPriceRecord.class);
    }

    @Override
    public List<PayDiscountRecord> listPayDiscountByBillingPlan(String billingPlan) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PayDiscountRecord.class))
                .where(DbUtil.column(PayDiscountRecord.class, "billingPlan"), "=", billingPlan);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectBySql(sqlParams, PayDiscountRecord.class);
    }

    @Override
    public PayQuotaTransactionRecord getPayQuotaTransactionByOutTradeNo(String outTradeNo) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PayQuotaTransactionRecord.class))
                .where(DbUtil.column(PayQuotaTransactionRecord.class, "outTradeNo"), "=", outTradeNo);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PayQuotaTransactionRecord.class);
    }

    @Override
    public PayQuotaTransactionRecord getPayQuotaTransactionByUserIdAndOutTradeNo(Long userId, String outTradeNo) {
        SqlParamsBuilder sqlParamsBuilder = new SqlParamsBuilder()
                .select("*")
                .from(DbUtil.table(PayQuotaTransactionRecord.class))
                .where(DbUtil.column(PayQuotaTransactionRecord.class, "userId"), "=", userId)
                .where(DbUtil.column(PayQuotaTransactionRecord.class, "outTradeNo"), "=", outTradeNo);

        SqlParams sqlParams = sqlParamsBuilder.build();

        return this.dbManager.selectFirstBySql(sqlParams, PayQuotaTransactionRecord.class);
    }

    @Override
    public boolean insertPayQuotaTransaction(PayQuotaTransactionRecord payQuotaTransactionRecord) {
        return this.dbManager.insert(payQuotaTransactionRecord) > 0;
    }

    @Override
    public boolean updatePayQuotaTransaction(PayQuotaTransactionRecord payQuotaTransactionRecord) {
        return this.dbManager.update(payQuotaTransactionRecord) > 0;
    }

}
