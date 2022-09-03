package com.airxiechao.y20.pipeline.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractPipelineRunContext {

    protected Map<String, PipelineVariable> projectVariables;
    protected Map<String, PipelineVariable> pipelineVariables = new HashMap<>();
    protected Map<String, String> pipelineRunInParams = new HashMap<>();

    public Set<String> getVariableNames() {
        return pipelineVariables.keySet();
    }

    public abstract String getVariableActualValue(String name);

    public abstract void setVariableValue(String name, String value);
}
