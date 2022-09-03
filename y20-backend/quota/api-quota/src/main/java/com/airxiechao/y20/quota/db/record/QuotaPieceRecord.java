package com.airxiechao.y20.quota.db.record;

import com.airxiechao.axcboot.storage.annotation.Index;
import com.airxiechao.axcboot.storage.annotation.Table;

import java.util.Date;

@Table(value = "quota_piece")
@Index(fields = {"userId", "beginTime", "endTime"})
@Index(fields = {"userId", "createTime"})
@Index(fields = {"userId", "payQuotaTransactionId"})
public class QuotaPieceRecord {
    private Long id;
    private Long userId;
    private int maxAgent;
    private int maxPipelineRun;
    private Date beginTime;
    private Date endTime;
    private Date createTime;
    private Long payQuotaTransactionId;

    public QuotaPieceRecord() {
    }

    public QuotaPieceRecord(Long id, Long userId, int maxAgent, int maxPipelineRun, Date beginTime, Date endTime,
                            Date createTime, Long payQuotaTransactionId
    ) {
        this.id = id;
        this.userId = userId;
        this.maxAgent = maxAgent;
        this.maxPipelineRun = maxPipelineRun;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.payQuotaTransactionId = payQuotaTransactionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getMaxAgent() {
        return maxAgent;
    }

    public void setMaxAgent(int maxAgent) {
        this.maxAgent = maxAgent;
    }

    public int getMaxPipelineRun() {
        return maxPipelineRun;
    }

    public void setMaxPipelineRun(int maxPipelineRun) {
        this.maxPipelineRun = maxPipelineRun;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getPayQuotaTransactionId() {
        return payQuotaTransactionId;
    }

    public void setPayQuotaTransactionId(Long payQuotaTransactionId) {
        this.payQuotaTransactionId = payQuotaTransactionId;
    }
}
