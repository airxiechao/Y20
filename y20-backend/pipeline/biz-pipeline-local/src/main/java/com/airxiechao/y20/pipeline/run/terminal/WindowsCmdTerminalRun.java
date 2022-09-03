package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.y20.pipeline.pojo.constant.EnumTerminalType;
import com.airxiechao.y20.pipeline.run.terminal.annotation.TerminalRun;

@TerminalRun(type = EnumTerminalType.TERMINAL_CMD)
public class WindowsCmdTerminalRun extends AbstractLocalTerminalRunInstance {

    private static final String[] CMD = { "cmd.exe" };

    public WindowsCmdTerminalRun(String workingDir) {
        super(CMD, workingDir);
    }

}
