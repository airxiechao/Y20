package com.airxiechao.y20.common.core.rest;

import com.airxiechao.y20.common.core.cache.PipelineRunInstanceServiceTagCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class ServicePipelineRunInstanceRestClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCallAgentClient.class);

    public static <T> T get(Class<T> cls, String pipelineRunInstanceUuid){
        String serviceTag = getServiceTag(pipelineRunInstanceUuid);
        return ServiceTaggedRestClient.get(cls, serviceTag);
    }

    public static <T> T get(Class<T> cls, String pipelineRunInstanceUuid, OutputStream outputStream){
        String serviceTag = getServiceTag(pipelineRunInstanceUuid);
        return ServiceTaggedRestClient.get(cls, serviceTag, outputStream);
    }

    private static String getServiceTag(String pipelineRunInstanceUuid){
        return PipelineRunInstanceServiceTagCache.getInstance().getPipelineRunInstanceServiceTag(pipelineRunInstanceUuid);
    }

}
