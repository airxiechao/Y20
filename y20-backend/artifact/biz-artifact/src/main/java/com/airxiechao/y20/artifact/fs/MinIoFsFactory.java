package com.airxiechao.y20.artifact.fs;

import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.minio.MinIoFs;
import com.airxiechao.y20.common.pojo.config.MinIoConfig;

public class MinIoFsFactory {
    private static final MinIoFsFactory instance = new MinIoFsFactory();
    public static MinIoFsFactory getInstance() {
        return instance;
    }

    private IFs fs;

    private MinIoFsFactory(){
        MinIoConfig minIoConfig = ConfigFactory.get(MinIoConfig.class);
        fs = new MinIoFs(
                minIoConfig.getEndpoint(),
                minIoConfig.getAccessKey(),
                minIoConfig.getSecretKey(),
                minIoConfig.getBucket(),
                minIoConfig.getThreadPoolCoreSize(),
                minIoConfig.getThreadPoolMaxSize(),
                minIoConfig.getThreadPoolQueueSize()
        );
    }

    public IFs getFs() {
        return fs;
    }
}
