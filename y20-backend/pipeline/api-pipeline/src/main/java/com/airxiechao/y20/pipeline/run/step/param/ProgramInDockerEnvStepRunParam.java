package com.airxiechao.y20.pipeline.run.step.param;

import com.airxiechao.axcboot.communication.common.annotation.Required;
import com.airxiechao.y20.pipeline.pojo.constant.EnumPipelineParamType;
import com.airxiechao.y20.pipeline.run.step.annotation.StepTypeParam;

public class ProgramInDockerEnvStepRunParam {

    @StepTypeParam(
            displayName = "命令行",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String command;

    @StepTypeParam(
            displayName = "工作目录",
            type = EnumPipelineParamType.INPUT)
    @Required
    private String hostWorkingDir;

    public ProgramInDockerEnvStepRunParam(String command, String hostWorkingDir) {
        this.command = command;
        this.hostWorkingDir = hostWorkingDir;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getHostWorkingDir() {
        return hostWorkingDir;
    }

    public void setHostWorkingDir(String hostWorkingDir) {
        this.hostWorkingDir = hostWorkingDir;
    }
}
