package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.y20.pipeline.run.util.LocalStepRunUtil;
import com.airxiechao.y20.util.ProcessUtil;
import org.apache.commons.lang.NotImplementedException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class AbstractLocalTerminalRunInstance implements ITerminalRunInstance {

    private String terminalRunInstanceUuid;
    private String[] cmd;
    private String workingDir;
    private Process pro;
    private InputStream inputStream;
    private OutputStream outputStream;

    public AbstractLocalTerminalRunInstance(String[] cmd, String workingDir){
        this.terminalRunInstanceUuid = UuidUtil.random();
        this.cmd = cmd;
        this.workingDir = workingDir;
    }

    @Override
    public void create() throws Exception {
        // read env variables from file (.env)
        Map<String, String> envVariables = LocalStepRunUtil.readEnvVariablesFromFile(workingDir);

        if(null == this.pro) {
            this.pro = ProcessUtil.startPtyProcess(cmd, workingDir, envVariables);
            this.inputStream = this.pro.getInputStream();
            this.outputStream = this.pro.getOutputStream();
        }
    }

    @Override
    public void destroy(){
        if(null == this.pro){
            throw new RuntimeException("terminal not start");
        }

        this.pro.destroy();
        this.pro = null;
        this.inputStream = null;
        this.outputStream = null;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void input(String text) throws Exception {
        outputStream.write(text.getBytes());
    }

    @Override
    public String getTerminalRunInstanceUuid() {
        return terminalRunInstanceUuid;
    }
}
