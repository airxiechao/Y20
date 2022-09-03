package com.airxiechao.y20.quota.biz.api;

import com.airxiechao.axcboot.core.annotation.IBiz;
import com.airxiechao.y20.quota.db.record.QuotaPieceRecord;
import com.airxiechao.y20.quota.pojo.Quota;
import com.airxiechao.y20.quota.pojo.Usage;
import com.airxiechao.y20.quota.pojo.vo.FreeQuotaVo;

import java.util.Date;
import java.util.List;

@IBiz
public interface IQuotaBiz {

    Usage measureUsage(Long userId, Date beginTime, Date endTime) throws Exception;

    FreeQuotaVo getFreeQuota();

    Quota getCurrentQuota(Long userId);

    void validateQuota(Long userId) throws Exception;

    QuotaPieceRecord createQuota(Long userId, int maxAgent, int maxPipelineRun, Date beginTime, Date endTime, Long payQuotaTransactionId);

    List<QuotaPieceRecord> listQuota(Long userId, String orderField, String orderType, Integer pageNo, Integer pageSize);
    long countQuota(Long userId);
}
