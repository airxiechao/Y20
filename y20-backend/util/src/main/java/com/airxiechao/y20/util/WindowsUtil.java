package com.airxiechao.y20.util;

import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.CharsetUtil;
import com.airxiechao.axcboot.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class WindowsUtil {

    private static final Logger logger = LoggerFactory.getLogger(WindowsUtil.class);

    public static final String EXECUTOR_CMD = "CMD";
    public static final String EXECUTOR_POWERSHELL = "POWERSHELL";
    public static final String EXECUTOR_UNKNOWN = "UNKNOWN";

    public static String getExecutorType(String executor){
        if(null != executor){
            executor = executor.toLowerCase().strip();
        }

        boolean isCmd = StringUtil.isBlank(executor) || "cmd".equals(executor) || "cmd.exe".equals(executor);
        if(isCmd){
            return EXECUTOR_CMD;
        }

        boolean isPowershell = "powershell".equals(executor) || "powershell.exe".equals(executor);
        if(isPowershell){
            return EXECUTOR_POWERSHELL;
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
        ProcessUtil.writeWindowsCommandString(process, "exit");

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
            executor = "cmd.exe";
        }
        Process process = ProcessUtil.startPtyProcess(new String[]{executor}, workingDir, envVariables);

        // write script
        //script = script.replaceAll("(?<!\r)\n", "\r\n");
        script = script.replaceAll("\n", "\r");
        ProcessUtil.writeWindowsCommandString(process, script);

        return process;
    }

    public static void exportEnvVariablesToFile(
            Process process,
            String workingDir,
            String[] variables,
            String executorType
    ){
        Path outputPath = Paths.get(workingDir, ".output");

        for (String variable : variables) {
            variable = variable.strip();
            if(StringUtil.isBlank(variable)){
                continue;
            }

            try {
                switch (executorType){
                    case EXECUTOR_CMD:
                        // cmd
                        ProcessUtil.writeWindowsCommandString(process, String.format("echo %s=%%%s%% >> %s", variable, variable, outputPath));
                        break;
                    case EXECUTOR_POWERSHELL:
                        // powershell
                        ProcessUtil.writeWindowsCommandString(process, String.format("echo %s=$%s | out-file %s -append -encoding utf8", variable, variable, outputPath));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                logger.error("export output variable [{}] error", variable, e);
            }
        }
    }

    public static void checkLastExitCode(Process process, String executorType){
        try {
            switch (executorType){
                case EXECUTOR_CMD:
                    // cmd
                    break;
                case EXECUTOR_POWERSHELL:
                    // powershell
                    ProcessUtil.writeWindowsCommandString(process, "if(!$?){ exit 1; }");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("check last exit code error", e);
        }
    }

    public static String buildHidePowershellPromptScript(){
        return formatHidePowershellPromptScript() + "\r";
    }

    public static String formatHidePowershellPromptScript(){
        return "function prompt { '> ' }; cls;";
    }
}
