package com.airxiechao.y20.pipeline.pojo.vo;

import com.airxiechao.y20.pipeline.db.record.PipelineStepRunRecord;
import com.airxiechao.y20.pipeline.pojo.PipelineRun;

import java.util.List;

public class PipelineRunDetailVo {
    private PipelineRun pipelineRun;
    private List<PipelineStepRunRecord> pipelineStepRuns;

    public PipelineRun getPipelineRun() {
        return pipelineRun;
    }

    public void setPipelineRun(PipelineRun pipelineRun) {
        this.pipelineRun = pipelineRun;
    }

    public List<PipelineStepRunRecord> getPipelineStepRuns() {
        return pipelineStepRuns;
    }

    public void setPipelineStepRuns(List<PipelineStepRunRecord> pipelineStepRuns) {
        this.pipelineStepRuns = pipelineStepRuns;
    }
}
