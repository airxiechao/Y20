package com.airxiechao.y20.util;

import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.CharsetUtil;
import com.airxiechao.axcboot.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LinuxUtil {

    private static final Logger logger = LoggerFactory.getLogger(LinuxUtil.class);

    public static final String EXECUTOR_BASH = "BASH";
    public static final String EXECUTOR_SH = "SH";
    public static final String EXECUTOR_UNKNOWN = "UNKNOWN";

    public static String getExecutorType(String executor){
        if(null != executor){
            executor = executor.toLowerCase().strip();
        }

        boolean isCmd = StringUtil.isBlank(executor) || "bash".equals(executor);
        if(isCmd){
            return EXECUTOR_BASH;
        }

        boolean isPowershell = "sh".equals(executor);
        if(isPowershell){
            return EXECUTOR_SH;
        }

        return EXECUTOR_UNKNOWN;
    }

    /**
     * 执行脚本
     */
    public static Long executeScript(
            String script,
            String executor,
            String workingDir,
            Map<String, String> envVariables,
            StreamUtil.PipedStream logStream
    ) throws Exception {
        Process process = executeScriptAsync(script, executor, workingDir, envVariables);
        ProcessUtil.writeLinuxCommandString(process, "exit");

        // read output
        StreamUtil.readStringInputStream(process.getInputStream(), 200, CharsetUtil.fromSystem(), (log)->{
            logStream.write(log);
            logStream.flush();
            logger.info(log.strip());
        });

        Long exitCode = Long.valueOf(process.waitFor());
        return exitCode;
    }

    /**
     * 异步执行脚本
     */
    public static Process executeScriptAsync(
            String script,
            String executor,
            String workingDir,
            Map<String, String> envVariables
    ) throws Exception {
        if(StringUtil.isBlank(executor)){
            executor = "bash";
        }
        Process process = ProcessUtil.startPtyProcess(new String[]{executor}, workingDir, envVariables);

        // write script
        ProcessUtil.writeLinuxCommandString(process, script);

        return process;
    }

    public static void exportEnvVariablesToFile(
            OutputStream outputStream,
            String workingDir,
            String[] variables
    ){
        Path outputPath = Paths.get(workingDir, ".output");

        for (String variable : variables) {
            variable = variable.strip();
            if(StringUtil.isBlank(variable)){
                continue;
            }

            try {
                ProcessUtil.writeLinuxCommandString(outputStream,
                        formatExportEnvVariableScript(variable, outputPath.toFile().getCanonicalFile().toString()));
            } catch (Exception e) {
                logger.error("export output variable [{}] error", variable, e);
            }
        }
    }

    public static void exportEnvVariablesToFile(
            Process process,
            String workingDir,
            String[] variables
    ){
        Path outputPath = Paths.get(workingDir, ".output");

        for (String variable : variables) {
            variable = variable.strip();
            if(StringUtil.isBlank(variable)){
                continue;
            }

            try {
                ProcessUtil.writeLinuxCommandString(process,
                        formatExportEnvVariableScript(variable, outputPath.toFile().getCanonicalFile().toString()));
            } catch (Exception e) {
                logger.error("export output variable [{}] error", variable, e);
            }
        }
    }

    public static String buildExportEnvVariablesToFileScript(
            String workingDir,
            String[] variables
    ){
        Path outputPath = Paths.get(workingDir, ".output");

        StringBuilder sb = new StringBuilder();
        for (String variable : variables) {
            variable = variable.strip();
            if (StringUtil.isBlank(variable)) {
                continue;
            }

            try {
                sb.append(
                        formatExportEnvVariableScript(variable, outputPath.toFile().getCanonicalFile().toString()) + "\n");
            } catch (Exception e) {
                logger.error("build export output variable [{}] script error", variable, e);
            }
        }

        return sb.toString();
    }

    public static void checkLastExitCode(OutputStream outputStream){
        try {
            ProcessUtil.writeLinuxCommandString(outputStream, formatCheckLastExitCodeScript());
        } catch (Exception e) {
            logger.error("check last exit code error", e);
        }
    }

    public static void checkLastExitCode(Process process){
        try {
            ProcessUtil.writeLinuxCommandString(process, formatCheckLastExitCodeScript());
        } catch (Exception e) {
            logger.error("check last exit code error", e);
        }
    }

    public static void hidePrompt(OutputStream outputStream){
        try {
            ProcessUtil.writeLinuxCommandString(outputStream, formatHidePromptScript());
        } catch (Exception e) {
            logger.error("hide prompt error", e);
        }
    }

    public static void hidePrompt(Process process){
        try {
            ProcessUtil.writeLinuxCommandString(process, formatHidePromptScript());
        } catch (Exception e) {
            logger.error("hide prompt error", e);
        }
    }

    public static String buildCheckLastExitCodeScript(){
        return formatCheckLastExitCodeScript() + "\n";
    }

    public static String buildHidePromptScript(){
        return formatHidePromptScript() + "\n";
    }

    public static String formatExportEnvVariableScript(String variable, String output) {
        return String.format("echo %s=$%s >> %s", variable, variable, output);
    }

    public static String formatCheckLastExitCodeScript(){
        return "if [ $? -ne 0 ]; then exit 1; fi";
    }

    public static String formatHidePromptScript(){
        return "export PS1='> '; export TERM='xterm'; clear;";
    }

}
