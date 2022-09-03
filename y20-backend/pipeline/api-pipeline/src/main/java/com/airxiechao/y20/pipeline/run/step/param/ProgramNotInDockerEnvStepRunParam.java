package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class ProgramNotInDockerEnvStepRunParam {

    @StepTypeParam(
            displayName = "命令行",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String command;

    @StepTypeParam(
            displayName = "工作目录",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String workingDir;

    public ProgramNotInDockerEnvStepRunParam(String command, String workingDir) {
        this.command = command;
        this.workingDir = workingDir;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
