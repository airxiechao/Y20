package com.airxiechao.y20.pipeline.run.terminal;

import com.airxiechao.axcboot.util.AnnotationUtil;
import com.airxiechao.axcboot.util.ClsUtil;
import com.airxiechao.y20.pipeline.pojo.constant.EnumTerminalType;
import com.airxiechao.y20.pipeline.run.terminal.annotation.TerminalRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalTerminalRunInstanceFactory {
    private static final Logger logger = LoggerFactory.getLogger(LocalTerminalRunInstanceFactory.class);

    private static final LocalTerminalRunInstanceFactory instance = new LocalTerminalRunInstanceFactory();
    public static LocalTerminalRunInstanceFactory getInstance(){
        return instance;
    }

    private final String PKG = LocalTerminalRunInstanceFactory.class.getPackageName();

    private Map<String, Class<? extends AbstractLocalTerminalRunInstance>> localRunTypeMap = new ConcurrentHashMap<>();
    private Map<String, AbstractLocalTerminalRunInstance> localTerminalRunMap = new ConcurrentHashMap<>();

    private LocalTerminalRunInstanceFactory(){
        // 注册 local terminal run
        Set<Class<? extends AbstractLocalTerminalRunInstance>> terminalTypes = ClsUtil.getSubTypesOf(PKG, AbstractLocalTerminalRunInstance.class);
        for(Class<? extends AbstractLocalTerminalRunInstance> cls : terminalTypes){
            registerLocalRunType(cls);
        }
    }

    private void registerLocalRunType(Class<? extends AbstractLocalTerminalRunInstance> cls){
        try {
            TerminalRun terminalRun = AnnotationUtil.getClassAnnotation(cls, TerminalRun.class);
            String type = terminalRun.type();
            localRunTypeMap.put(type, cls);
        } catch (Exception e) {

        }
    }

    public AbstractLocalTerminalRunInstance createLocalTerminalRun(String type, String dockerContainerId, String workingDir) throws Exception {
        if(!localRunTypeMap.containsKey(type)){
            throw new Exception(String.format("no terminal run type [%s]", type));
        }

        AbstractLocalTerminalRunInstance terminalRun;
        Class<? extends AbstractLocalTerminalRunInstance> cls = localRunTypeMap.get(type);
        if(EnumTerminalType.TERMINAL_DOCKER.equals(type)){
            terminalRun = cls.getDeclaredConstructor(String.class, String.class).newInstance(dockerContainerId, workingDir);
        }else{
            terminalRun = cls.getDeclaredConstructor(String.class).newInstance(workingDir);
        }

        this.localTerminalRunMap.put(terminalRun.getTerminalRunInstanceUuid(), terminalRun);

        terminalRun.create();

        return terminalRun;
    }

    public void destroyLocalTerminalRun(String terminalRunId){
        AbstractLocalTerminalRunInstance terminalRun = localTerminalRunMap.get(terminalRunId);
        if(null != terminalRun){
            terminalRun.destroy();
            localTerminalRunMap.remove(terminalRunId);
        }
    }

    public AbstractLocalTerminalRunInstance getLocalTerminalRun(String terminalRunId){
        return this.localTerminalRunMap.get(terminalRunId);
    }

}
