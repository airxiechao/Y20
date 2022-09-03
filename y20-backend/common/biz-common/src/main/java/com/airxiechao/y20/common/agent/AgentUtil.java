package com.airxiechao.y20.common.agent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgentUtil {
    private static Pattern accessTokenPattern = Pattern.compile("accessToken:\\s*(\\S+)\\s*");

    public static String extractAccessTokenFromConfig(String config){
        for (String line : config.split("\n")) {
            line = line.strip();
            if(line.startsWith("#")){
                continue;
            }

            Matcher matcher = Pattern.compile("^accessToken:\\s*(\\S+)$").matcher(line);
            if(matcher.find()){
                return matcher.group(1);
            }
        }

        return null;
    }
}
