package com.airxiechao.y20.util;

import com.airxiechao.axcboot.util.FileUtil;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProcessUtil {

    public static Process startProcess(
            String[] commands,
            String workingDir,
            Map<String, String> envVariables
    ) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        if(null != workingDir) {
            FileUtil.mkDirs(workingDir);
            builder.directory(new File(workingDir));
        }
        if(null != envVariables){
            Map<String, String> environment = builder.environment();
            environment.putAll(envVariables);
        }
        Process process = builder.start();
        return process;
    }

    public static Process startPtyProcess(
            String[] commands,
            String workingDir,
            Map<String, String> envVariables
    ) throws Exception {
        if(null != workingDir) {
            FileUtil.mkDirs(workingDir);
        }

        Map<String, String> environment = new ProcessBuilder().environment();
        if(null != envVariables){
            environment.putAll(envVariables);
        }

        PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder();
        ptyProcessBuilder.setRedirectErrorStream(true);
        ptyProcessBuilder.setCommand(commands);
        ptyProcessBuilder.setEnvironment(environment);
        ptyProcessBuilder.setDirectory(workingDir);

        PtyProcess pty = ptyProcessBuilder.start();
        return pty;
    }

    public static void writeWindowsCommandString(OutputStream outputStream, String command) throws IOException {
        outputStream.write(command.getBytes());
        outputStream.write('\r');
        //outputStream.write('\n');
        outputStream.flush();
    }

    public static void writeWindowsCommandString(Process process, String command) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        writeWindowsCommandString(outputStream, command);
    }

    public static void writeLinuxCommandString(OutputStream outputStream, String command) throws IOException {
        outputStream.write(command.getBytes());
        outputStream.write('\n');
        outputStream.flush();
    }

    public static void writeLinuxCommandString(Process process, String command) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        writeLinuxCommandString(outputStream, command);
    }

    public static void writeWindowsCommandByte(OutputStream outputStream, int command) throws IOException {
        outputStream.write(command);
        outputStream.write('\r');
        //outputStream.write('\n');
        outputStream.flush();
    }

    public static void writeWindowsCommandByte(Process process, int command) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        writeWindowsCommandByte(outputStream, command);
    }

    public static void writeLinuxCommandByte(OutputStream outputStream, int command) throws IOException {
        outputStream.write(command);
        outputStream.write('\n');
        outputStream.flush();
    }

    public static void writeLinuxCommandByte(Process process, int command) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        writeLinuxCommandByte(outputStream, command);
    }

    public static void writeBytes(Process process, byte[] bytes) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        outputStream.write(bytes);
    }

    public static String[] splitCommandString(String command){
        String[] arr = command.split("(?<!\\\\)\\s+");
        return arr;
    }
}
