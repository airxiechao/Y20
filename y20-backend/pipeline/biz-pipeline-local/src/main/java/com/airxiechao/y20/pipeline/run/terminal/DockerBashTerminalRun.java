package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.y20.pipeline.pojo.constant.EnumTerminalType;
import com.airxiechao.y20.pipeline.run.terminal.annotation.TerminalRun;

@TerminalRun(type = EnumTerminalType.TERMINAL_DOCKER)
public class DockerBashTerminalRun extends AbstractLocalTerminalRunInstance {

    public DockerBashTerminalRun(String dockerContainerId, String workingDir) {
        super(new String[]{
                "docker", "exec", "-it", dockerContainerId, "bash"
        }, workingDir);
    }
}
