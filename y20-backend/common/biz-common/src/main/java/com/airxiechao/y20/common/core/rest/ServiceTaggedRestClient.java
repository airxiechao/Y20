package com.airxiechao.y20.common.core.rest;

import com.airxiechao.axcboot.communication.rest.util.RestClientUtil;
import com.airxiechao.axcboot.communication.rest.util.RestUtil;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.axcboot.core.rest.AbstractRestClient;
import com.airxiechao.axcboot.util.MapBuilder;
import com.airxiechao.axcboot.util.StringUtil;
import com.airxiechao.y20.common.pojo.config.AuthCommonConfig;
import com.airxiechao.y20.common.core.cache.RestServiceCache;
import com.ecwid.consul.v1.health.model.HealthService;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;

public class ServiceTaggedRestClient extends AbstractRestClient {

    private static final ServiceTaggedRestClient instance = new ServiceTaggedRestClient();
    public static ServiceTaggedRestClient getInstance() {
        return instance;
    }

    private static AuthCommonConfig authServiceConfig = ConfigFactory.get(AuthCommonConfig.class);
    private static final String SERVICE_NAME_PREFIX = "y20-backend-";

    public static <T> T get(Class<T> cls, String serviceTag){
        String accessToken = authServiceConfig.getServiceAccessToken();
        Map<String, String> headers = new MapBuilder<String, String>()
                .put("auth", accessToken)
                .build();

        return instance.getProxy(cls, serviceTag, headers, null, 0, false);
    }

    public static <T> T get(Class<T> cls, String serviceTag, OutputStream outputStream){
        String accessToken = authServiceConfig.getServiceAccessToken();
        Map<String, String> headers = new MapBuilder<String, String>()
                .put("auth", accessToken)
                .build();

        return instance.getProxy(cls, serviceTag, headers, null, 0, false, outputStream, null, null);
    }

    @Override
    public String getUrl(Method method, String serviceTag) throws Exception{
        String serviceName = RestServiceCache.getInstance().getMethodServiceName(method);
        if(StringUtil.isBlank(serviceName)){
            throw new Exception("no service name of " + method.getName());
        }

        serviceName = SERVICE_NAME_PREFIX+serviceName;
        HealthService service = RestClientUtil.getServiceFromConsul(serviceName, serviceTag);

        String host = service.getNode().getAddress();
        int port = service.getService().getPort();
        Map<String, String> meta = service.getService().getMeta();
        String basePath = meta.getOrDefault("basePath", "");

        return String.format("http://%s:%d%s%s", host, port, basePath, RestUtil.getMethodPath(method));
    }
}
