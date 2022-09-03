package com.airxiechao.y20.pipeline.run.step;

import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.y20.common.pipeline.env.Env;
import com.airxiechao.y20.pipeline.pojo.PipelineStep;
import com.airxiechao.y20.pipeline.run.step.annotation.StepRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalStepRunInstanceFactory {
    private static final Logger logger = LoggerFactory.getLogger(LocalStepRunInstanceFactory.class);

    private static final LocalStepRunInstanceFactory instance = new LocalStepRunInstanceFactory();
    public static LocalStepRunInstanceFactory getInstance(){
        return instance;
    }

    private final String PKG = LocalStepRunInstanceFactory.class.getPackageName();

    private Map<String, Class<? extends AbstractStepRunInstance>> runClsMap = new HashMap<>();
    private Map<String, AbstractStepRunInstance> localRunMap = new ConcurrentHashMap<>();

    private LocalStepRunInstanceFactory() {
        // 注册 step-run
        Set<Class<?>> runTypes = ClsUtil.getTypesAnnotatedWith(PKG, StepRun.class);
        runTypes.forEach(this::registerRun);
    }

    public void registerRun(Class<?> cls){
        try {
            if(AbstractStepRunInstance.class.isAssignableFrom(cls)){
                StepRun stepRun = cls.getAnnotation(StepRun.class);
                String type = stepRun.type();
                runClsMap.put(type, (Class<? extends AbstractStepRunInstance>)cls);
            }

        } catch (Exception e) {

        }
    }

    public AbstractStepRunInstance buildLocalRun(PipelineStep step, String stepRunInstanceUuid, Env env) {
        try{
            var runCls = runClsMap.get(step.getType());
            if(null == runCls){
                throw new Exception(String.format("no step type [%s]", step.getType()));
            }
            Constructor<? extends AbstractStepRunInstance> constructor = runCls.getConstructor(String.class, Env.class);
            AbstractStepRunInstance run = constructor.newInstance(stepRunInstanceUuid, env);
            run.assemble(step);

            addLocalRun(run);

            return run;
        }catch (Exception e){
            logger.error("build local run error", e);
            throw new RuntimeException(e);
        }
    }

    private void addLocalRun(AbstractStepRunInstance run){
        localRunMap.put(run.getStepRunInstanceUuid(), run);
    }

    public AbstractStepRunInstance getLocalRun(String stepRunId){
        return localRunMap.get(stepRunId);
    }

    public void removeLocalRun(String stepRunId){
        localRunMap.remove(stepRunId);
    }

}
