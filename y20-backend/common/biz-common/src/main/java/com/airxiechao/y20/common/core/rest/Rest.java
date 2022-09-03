package com.airxiechao.y20.common.core.rest;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.core.rest.RestReg;
import com.airxiechao.y20.common.core.cache.RestServiceCache;

public class Rest extends RestReg {

    public Rest(String pkg, RestServer restServer) {
        super(pkg, restServer);
    }

    public Rest(String pkg, RestServer restServer, Class[] exclusion) {
        super(pkg, restServer, exclusion);
    }

    @Override
    protected void afterRegisterHandler(Class<?> cls, RestServer restServer) {
        RestServiceCache.getInstance().setMethodServiceName(cls, restServer.getName());
    }
}
