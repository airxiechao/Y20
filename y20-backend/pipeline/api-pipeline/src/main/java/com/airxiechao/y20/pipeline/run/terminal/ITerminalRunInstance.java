package com.airxiechao.y20.pipeline.run.terminal;

import java.io.InputStream;
import java.io.OutputStream;

public interface ITerminalRunInstance {
    void create() throws Exception;
    void destroy() throws Exception;
    void input(String text) throws Exception;
    InputStream getInputStream();
    OutputStream getOutputStream();
    String getTerminalRunInstanceUuid();
}
