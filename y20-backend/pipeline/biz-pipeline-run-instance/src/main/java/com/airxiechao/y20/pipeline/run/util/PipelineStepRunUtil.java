package com.airxiechao.y20.pipeline.run.util;

import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipelineStepRunUtil {

    private static final Pattern holderPattern = Pattern.compile("\\{\\{\\s*([^\\{\\}\\s]+)\\s*\\}\\}");

    public static void replaceHolderByContext(Map variables, AbstractPipelineRunContext pipelineRunContext){
        if(null != variables){
            // replace holder by variable
            Set<String> variableNames = pipelineRunContext.getVariableNames();
            Set keys = variables.keySet();
            keys.forEach(key -> {
                Object paramValue = variables.get(key);
                if(key instanceof String && paramValue instanceof String){
                    String paramName = (String)key;
                    String strValue = (String)paramValue;

                    Set<String> holders = new HashSet<>();
                    Matcher mat = holderPattern.matcher(strValue);
                    while (mat.find()) {
                        String holder = mat.group(1);
                        if (variableNames.contains(holder)) {
                            holders.add(holder);
                        }
                    }

                    for (String holder : holders) {
                        String variableActualValue = pipelineRunContext.getVariableActualValue(holder);
                        if (null == variableActualValue) {
                            variableActualValue = "";
                            //throw new RuntimeException(String.format("variable holder [%s]'s value not found", holder));
                        }
                        strValue = strValue.replaceAll("\\{\\{\\s*" + holder + "\\s*\\}\\}", variableActualValue);
                    }

                    variables.put(paramName, strValue);
                }
            });
        }
    }
}
