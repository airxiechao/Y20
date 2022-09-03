package com.airxiechao.y20.quota.db.api;

import com.airxiechao.axcboot.core.annotation.IDb;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;

import java.util.Date;
import java.util.List;

@IDb("mybatis-y20-quota.xml")
public interface IQuotaDb {
    QuotaPieceRecord getAggregatedQuota(Long userId, Date time);
    boolean createQuota(QuotaPieceRecord quotaPieceRecord);
    List<QuotaPieceRecord> listQuota(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countQuota(Long userId);
}
