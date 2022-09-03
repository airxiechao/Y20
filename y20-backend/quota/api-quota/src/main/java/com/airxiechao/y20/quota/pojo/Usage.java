package com.airxiechao.y20.quota.pojo;

public class Usage {
    private int numAgent;
    private int numPipelineRun;

    public Usage(int numAgent, int numPipelineRun) {
        this.numAgent = numAgent;
        this.numPipelineRun = numPipelineRun;
    }

    public int getNumAgent() {
        return numAgent;
    }

    public void setNumAgent(int numAgent) {
        this.numAgent = numAgent;
    }

    public int getNumPipelineRun() {
        return numPipelineRun;
    }

    public void setNumPipelineRun(int numPipelineRun) {
        this.numPipelineRun = numPipelineRun;
    }
}
