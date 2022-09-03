package com.airxiechao.y20.pipeline.pojo.vo;

public class PipelineCountVo {
    private Long total;
    private Long running;

    public PipelineCountVo(Long total, Long running) {
        this.total = total;
        this.running = running;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getRunning() {
        return running;
    }

    public void setRunning(Long running) {
        this.running = running;
    }
}
