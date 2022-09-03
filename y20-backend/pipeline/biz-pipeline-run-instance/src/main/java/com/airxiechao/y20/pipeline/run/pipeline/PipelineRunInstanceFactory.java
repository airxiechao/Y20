package com.airxiechao.y20.pipeline.run.pipeline;

import com.airxiechao.axcboot.process.threadpool.ThreadPool;
import com.airxiechao.axcboot.process.threadpool.ThreadPoolManager;
import com.airxiechao.y20.pipeline.pojo.vo.PipelineRunDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PipelineRunInstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PipelineRunInstanceFactory.class);

    private static final PipelineRunInstanceFactory instance = new PipelineRunInstanceFactory();
    public static PipelineRunInstanceFactory getInstance() {
        return instance;
    }

    private static final ThreadPool threadPool = ThreadPoolManager.getInstance().getThreadPool(
            "pipeline-run-thread-pool", 10, 50, 10);

    private Map<String, IPipelineRunInstance> pipelineRunInstanceMap = new ConcurrentHashMap<>();

    private PipelineRunInstanceFactory(){}

    public IPipelineRunInstance buildPipelineRunInstance(
            PipelineRunDetailVo pipelineRunDetail
    ) {
        IPipelineRunInstance pipelineRunInstance = new PipelineRunInstance(
                pipelineRunDetail,
                threadPool.getExecutor());

        pipelineRunInstanceMap.put(pipelineRunInstance.getPipelineRunInstanceUuid(), pipelineRunInstance);
        return pipelineRunInstance;
    }

    public void destroyPipelineRunInstance(String pipelineRunInstanceUuid) {
        pipelineRunInstanceMap.remove(pipelineRunInstanceUuid);
    }

    public IPipelineRunInstance getPipelineRunInstance(String pipelineRunInstanceUuid){
        return pipelineRunInstanceMap.get(pipelineRunInstanceUuid);
    }

    public List<IPipelineRunInstance> listPipelineRunInstance(){
        return pipelineRunInstanceMap.values().stream().collect(Collectors.toList());
    }

    public void shutdown(){
        logger.info("pipeline run instance factory shutdown");

        pipelineRunInstanceMap.entrySet().forEach(entry -> {
            String pipelineRunInstanceUuid = entry.getKey();
            IPipelineRunInstance pipelineRunInstance = entry.getValue();
            pipelineRunInstance.stop();
        });

        threadPool.shutdownGracefully();
    }

}
