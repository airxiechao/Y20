package com.airxiechao.y20.log.rest.server;

import com.airxiechao.axcboot.communication.common.Response;
import com.airxiechao.axcboot.communication.rest.server.RestServer;
import com.airxiechao.axcboot.config.factory.ConfigFactory;
import com.airxiechao.y20.auth.rest.api.IServiceAuthRest;
import com.airxiechao.y20.auth.rest.param.ValidateAccessTokenParam;
import com.airxiechao.y20.common.pojo.constant.meta.Meta;
import com.airxiechao.y20.common.core.rest.Rest;
import com.airxiechao.y20.common.core.rest.ServiceRestClient;
import com.airxiechao.y20.log.pojo.config.LogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogRestServer extends RestServer {
    private static final Logger logger = LoggerFactory.getLogger(LogRestServer.class);

    private static final LogConfig config = ConfigFactory.get(LogConfig.class);
    public static final String NAME = config.getName();
    public static final int PORT = config.getPort();

    private static final LogRestServer instance = new LogRestServer();

    public static LogRestServer getInstance() {
        return instance;
    }

    private LogRestServer() {
        super(NAME);

        this.config("0.0.0.0", PORT, null, null, null,
                (token, scope, item, mode) -> {
                    ValidateAccessTokenParam validation = new ValidateAccessTokenParam(
                            token, scope, item, mode
                    );
                    Response resp = ServiceRestClient.get(IServiceAuthRest.class).validateAccessToken(validation);

                    return resp.isSuccess();
                });

        // consul
        this.registerConsul(10, "y20-backend-");

        // rest
        Rest rest = new Rest(Meta.getModulePackageName(this.getClass()), this);
        rest.registerHandlerIfExists();

    }
}
