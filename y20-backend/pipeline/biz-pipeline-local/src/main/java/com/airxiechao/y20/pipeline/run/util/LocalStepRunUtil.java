package com.airxiechao.y20.pipeline.run.util;

import com.airxiechao.axcboot.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalStepRunUtil {

    private static final Logger logger = LoggerFactory.getLogger(LocalStepRunUtil.class);

    public static void writeEnvVariablesToFile(Map<String, String> envVariables, String workingDir){
        try {
            Files.writeString(Paths.get(workingDir, ".env"), JSON.toJSONString(envVariables));
        } catch (IOException e) {
            logger.error("write .env file error", e);
        }
    }

    public static Map<String, String> readEnvVariablesFromFile(String workingDir){
        Map<String, String> envVariables = null;
        Path envPath = Paths.get(workingDir, ".env");
        if(Files.exists(envPath)){
            String strEnv = null;
            try {
                strEnv = Files.readString(envPath);
            } catch (IOException e) {
                logger.error("read .env file error", e);
            }
            if(!StringUtil.isBlank(strEnv)){
                envVariables = JSON.parseObject(strEnv, new TypeReference<>(){});
            }
        }

        return envVariables;
    }

    public static Map<String, String> readOutputVariablesFromFile(String workingDir){
        Path outputPath = Paths.get(workingDir, ".output");

        Map<String, String> exported = new HashMap<>();
        if(!Files.exists(outputPath)){
            return exported;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(outputPath);
        } catch (IOException e) {
            logger.error("read .output file error", e);
            return exported;
        } finally {
            if(!outputPath.toFile().delete()){
                logger.error("delete .output file error");
            }
        }

        lines.forEach(line -> {
            line = line.strip();
            int i = line.indexOf("=");
            String name = line.substring(0, i).strip();
            String value = line.substring(i+1).strip();
            exported.put(name, value);
        });

        return exported;
    }
}
