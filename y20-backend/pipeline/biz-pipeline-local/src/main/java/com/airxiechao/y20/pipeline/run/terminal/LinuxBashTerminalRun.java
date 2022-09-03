package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.y20.pipeline.pojo.constant.EnumTerminalType;
import com.airxiechao.y20.pipeline.run.terminal.annotation.TerminalRun;

@TerminalRun(type = EnumTerminalType.TERMINAL_BASH)
public class LinuxBashTerminalRun extends AbstractLocalTerminalRunInstance {

    private static final String[] CMD = { "/bin/bash" };

    public LinuxBashTerminalRun(String workingDir) {
        super(CMD, workingDir);
    }
}
