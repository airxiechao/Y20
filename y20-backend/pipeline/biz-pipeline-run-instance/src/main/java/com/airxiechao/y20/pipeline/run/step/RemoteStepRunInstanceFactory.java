package com.airxiechao.y20.pipeline.run.step;

import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.AbstractPipelineRunContext;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteStepRunInstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(RemoteStepRunInstanceFactory.class);

    private static final RemoteStepRunInstanceFactory instance = new RemoteStepRunInstanceFactory();
    public static RemoteStepRunInstanceFactory getInstance(){
        return instance;
    }

    private final String PKG = RemoteStepRunInstanceFactory.class.getPackageName();

    private Map<String, Class<? extends RemoteStepRunInstance>> runClsMap = new HashMap<>();
    private Map<String, RemoteStepRunInstance> localRunMap = new ConcurrentHashMap<>();

    private RemoteStepRunInstanceFactory() {
        // 注册 step-run
        Set<Class<?>> runTypes = ClsUtil.getTypesAnnotatedWith(PKG, StepRun.class);
        runTypes.forEach(this::registerRun);
    }

    public void registerRun(Class<?> cls){
        try {
            if(RemoteStepRunInstance.class.isAssignableFrom(cls)){
                StepRun stepRun = cls.getAnnotation(StepRun.class);
                String type = stepRun.type();
                runClsMap.put(type, (Class<? extends RemoteStepRunInstance>)cls);
            }

        } catch (Exception e) {

        }
    }

    public RemoteStepRunInstance buildRemoteRun(PipelineStep step, String stepRunInstanceUuid, Env env, AbstractPipelineRunContext pipelineRunContext) {
        try{
            var runCls = runClsMap.get(step.getType());
            if(null == runCls){
                throw new Exception(String.format("no remote step type [%s]", step.getType()));
            }

            RemoteStepRunInstance run = runCls
                    .getConstructor(String.class, Env.class, AbstractPipelineRunContext.class)
                    .newInstance(stepRunInstanceUuid, env, pipelineRunContext);
            run.assemble(step);

            return run;
        }catch (Exception e){
            logger.error("build remote run error", e);
            throw new RuntimeException(e);
        }
    }

}

