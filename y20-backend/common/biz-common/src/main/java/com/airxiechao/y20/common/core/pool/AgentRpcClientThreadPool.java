package com.airxiechao.y20.common.core.pool;

import com.airxiechao.axcboot.process.threadpool.ThreadPool;
import com.airxiechao.axcboot.process.threadpool.ThreadPoolManager;

public class AgentRpcClientThreadPool {
    private static final ThreadPool threadPool = ThreadPoolManager.getInstance().getThreadPool(
            "agent-rpc-client-thread-pool", 20, 1000, 1);

    public static ThreadPool get(){
        return threadPool;
    }
}
