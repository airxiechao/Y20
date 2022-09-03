package com.airxiechao.y20.pipeline.pojo.vo;

import java.util.List;

public class PipelineBasicPageVo {
    private List<PipelineBasicVo> page;
    private boolean hasMore;

    public PipelineBasicPageVo() {
    }

    public PipelineBasicPageVo(List<PipelineBasicVo> page, boolean hasMore) {
        this.page = page;
        this.hasMore = hasMore;
    }

    public List<PipelineBasicVo> getPage() {
        return page;
    }

    public void setPage(List<PipelineBasicVo> page) {
        this.page = page;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
