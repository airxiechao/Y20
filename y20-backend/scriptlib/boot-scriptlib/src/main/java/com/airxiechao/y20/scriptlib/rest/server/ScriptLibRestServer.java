package com.airxiechao.y20.scriptlib.rest.server;

import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.common.core.rest.EnhancedRestUtil;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.scriptlib.pojo.config.ScriptLibConfig;
import com.airxiechao.y20.scriptlib.rest.handler.UserScriptLibRestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptLibRestServer extends RestServer {

    private static final Logger logger = LoggerFactory.getLogger(ScriptLibRestServer.class);
    private static final ScriptLibConfig config = ConfigFactory.get(ScriptLibConfig.class);

    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final ScriptLibRestServer instance = new ScriptLibRestServer();

    public static ScriptLibRestServer getInstance() {
        return instance;
    }

    private ScriptLibRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                EnhancedRestUtil::validateAccessToken,
                (exchange, method) -> {
                    if(method.getDeclaringClass().getPackage().getName().startsWith(Meta.getModulePackageName(this.getClass()))){
                        return EnhancedRestUtil.resolveParam(exchange, method, true);
                    }else{
                        return exchange;
                    }
                }
        );

        // register consul
        this.registerConsul(10, "y20-backend-");

        // register rest and ws
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();
        rest.registerWsIfExists();

    }
}
