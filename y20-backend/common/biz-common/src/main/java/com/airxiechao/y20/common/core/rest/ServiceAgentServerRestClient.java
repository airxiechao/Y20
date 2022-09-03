package com.airxiechao.y20.common.core.rest;

import com.airxiechao.y20.common.core.cache.AgentServiceTagCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceAgentServerRestClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAgentServerRestClient.class);

    public static <T> T get(Class<T> cls, String clientId){
        String serviceTag = getServiceTag(clientId);
        return ServiceTaggedRestClient.get(cls, serviceTag);
    }

    private static String getServiceTag(String clientId){
        return AgentServiceTagCache.getInstance().getAgentServiceTag(clientId);
    }
}
