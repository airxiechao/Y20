package com.airxiechao.y20.pipeline.run.explorer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalExplorerRunInstanceFactory {

    private static final LocalExplorerRunInstanceFactory instance = new LocalExplorerRunInstanceFactory();
    public static LocalExplorerRunInstanceFactory getInstance() {
        return instance;
    }

    private Map<String, AbstractLocalExplorerRunInstance> localExplorerRunMap = new ConcurrentHashMap<>();

    private LocalExplorerRunInstanceFactory(){}

    public IExplorerRunInstance createLocalExplorerRun(String workingDir) throws Exception {
        AbstractLocalExplorerRunInstance explorerRun = new LocalExplorerRunInstance(workingDir);;
        this.localExplorerRunMap.put(explorerRun.getExplorerRunInstanceUuid(), explorerRun);
        return explorerRun;
    }

    public void destroyLocalExplorerRun(String explorerRunInstanceUuid) throws Exception {
        AbstractLocalExplorerRunInstance explorer = this.localExplorerRunMap.get(explorerRunInstanceUuid);
        if(null != explorer){
            explorer.destroy();
            this.localExplorerRunMap.remove(explorerRunInstanceUuid);
        }
    }

    public AbstractLocalExplorerRunInstance getLocalExplorerRun(String explorerRunInstanceUuid){
        return localExplorerRunMap.get(explorerRunInstanceUuid);
    }

}